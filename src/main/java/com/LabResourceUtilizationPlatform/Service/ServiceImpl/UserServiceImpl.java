package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateUserRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateUserRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.UserResponse;
import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Entity.Role;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.DepartmentRepository;
import com.LabResourceUtilizationPlatform.Repository.InstitutionRepository;
import com.LabResourceUtilizationPlatform.Repository.RoleRepository;
import com.LabResourceUtilizationPlatform.Repository.UserRepository;
import com.LabResourceUtilizationPlatform.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OtpServiceImpl otpService;
    private final EmailServiceImpl emailService;
    private final RoleRepository roleRepository;
    private final InstitutionRepository institutionRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists.");
        }
        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new RuntimeException("Phone number already exists.");
        }
        if(userRepository.existsByRegistrationId(request.getRegistrationId())){
            throw new RuntimeException("Registration ID already exists.");
        }


        //Finding either role exists or not
        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found."));

        //Finding either Institution exists or not
        Institution institution = institutionRepository.findByCode(request.getInstitutionCode())
                .orElseThrow(()->new RuntimeException("Institution not found."));

        //Finding either Department exists or not
        Department department = departmentRepository
                .findByNameAndInstitution_Code(
                        request.getDepartmentName(),
                        request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Department not found."));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .registrationId(request.getRegistrationId())
                .role(role)
                .emailVerified(false)
                .institution(institution)
                .department(department)
                .build();


        // Generate OTP and send email
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        User savedUser = userRepository.save(user);

        emailService.sendOtpByEmail(request.getEmail(),otp);

        logger.info("User Registered: {}",request.getEmail());

        UserResponse response = modelMapper.map(savedUser,UserResponse.class);
        response.setInstitution(savedUser.getInstitution().getName());
        response.setRole(savedUser.getRole().getRoleName().name());
        response.setDepartment(savedUser.getDepartment().getName());
        return response;
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found."));

        UserResponse userResponse = modelMapper.map(user,UserResponse.class);

        userResponse.setRole(user.getRole().getRoleName().name());
        userResponse.setInstitution(user.getInstitution().getName());
        userResponse.setDepartment(user.getDepartment().getName());
        return userResponse;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user-> {
                    UserResponse userResponse = modelMapper.map(user,UserResponse.class);
                    userResponse.setRole(user.getRole().getRoleName().name());
                    userResponse.setInstitution(user.getInstitution().getName());
                    userResponse.setDepartment(user.getDepartment().getName());
                    return userResponse;
                }).toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(UpdateUserRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Update email if provided
        if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {

            if (!user.getEmail().equals(request.getNewEmail())
                    && userRepository.existsByEmail(request.getNewEmail())) {
                throw new RuntimeException("Email already exists.");
            }

            user.setEmail(request.getNewEmail());
        }

        // Validate phone number
        if (!user.getPhoneNumber().equals(request.getPhoneNumber())
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists.");
        }

        // Validate registration ID
        if (!user.getRegistrationId().equals(request.getRegistrationId())
                && userRepository.existsByRegistrationId(request.getRegistrationId())) {
            throw new RuntimeException("Registration ID already exists.");
        }

        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found."));

        Institution institution = institutionRepository
                .findByCode(request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Institution not found."));

        Department department = departmentRepository
                .findByNameAndInstitution_Code(
                        request.getDepartmentName(),
                        request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Department not found."));

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRegistrationId(request.getRegistrationId());
        user.setRole(role);
        user.setInstitution(institution);
        user.setDepartment(department);

        // Optional password update
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        logger.info("User updated: {}", updatedUser.getEmail());

        UserResponse response = modelMapper.map(updatedUser, UserResponse.class);
        response.setRole(updatedUser.getRole().getRoleName().name());
        response.setInstitution(updatedUser.getInstitution().getName());
        response.setDepartment(updatedUser.getDepartment().getName());

        return response;
    }

    @Override
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        logger.info("User Deleted : {}", user.getEmail());
    }
}
