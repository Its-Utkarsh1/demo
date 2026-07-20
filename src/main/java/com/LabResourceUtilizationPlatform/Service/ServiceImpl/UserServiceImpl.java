package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateUserRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateUserRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.UserResponse;
import com.LabResourceUtilizationPlatform.Entity.*;
import com.LabResourceUtilizationPlatform.Entity.Enum.RoleName;
import com.LabResourceUtilizationPlatform.Repository.*;
import com.LabResourceUtilizationPlatform.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
    private final LabRepository labRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists.");
        }
        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new RuntimeException("Phone number already exists.");
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
                .role(role)
                .emailVerified(false)
                .institution(institution)
                .department(department)
                .build();


        User savedUser = userRepository.save(user);

        if (request.getRole() == RoleName.LAB_MANAGER) {

            if (request.getLabCode() == null || request.getLabCode().isBlank()) {
                throw new RuntimeException("Lab code is required for Lab Manager");
            }

            Lab lab = labRepository.findByLabCode(request.getLabCode())
                    .orElseThrow(() -> new RuntimeException("Lab not found"));

            if (lab.getLabManager() != null) {
                throw new RuntimeException("This lab already has a manager.");
            }

            lab.setLabManager(savedUser);
            labRepository.save(lab);
        }

        String otp = otpService.generateOtp();
        redisTemplate.opsForValue().set(
                "otp:" + savedUser.getEmail(),
                otp,
                Duration.ofMinutes(10)
        );
        try {
            emailService.sendOtpByEmail(savedUser.getEmail(), otp);
        } catch (Exception ex) {
            redisTemplate.delete("otp:" + savedUser.getEmail());
            throw ex;
        }
        logger.info("User Registered: {}",request.getEmail());

        return mapToResponse(savedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found."));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getLabTechniciansForManager() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        System.out.println("Authentication Name = " + authentication.getName());
        System.out.println("Authentication Principal = " + authentication.getPrincipal());

        User manager = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found."));

        System.out.println("Manager Email = " + manager.getEmail());
        System.out.println("Manager Department = " + manager.getDepartment().getName());

        List<User> technicians = userRepository
                .findByDepartment_IdAndRole_RoleName(
                        manager.getDepartment().getId(),
                        RoleName.LAB_TECHNICIAN
                );

        System.out.println("Technicians Found = " + technicians.size());

        return technicians.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getUsersByDepartment(
            String institutionCode,
            String departmentName) {

        return userRepository
                .findByInstitution_CodeAndDepartment_Name(
                        institutionCode,
                        departmentName
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<UserResponse> getAllUserByInstitutionCode(String institutionCode) {
        return userRepository.findByInstitution_Code(institutionCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#request.email")
    public UserResponse updateUser(UpdateUserRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
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
        user.setRole(role);
        user.setInstitution(institution);
        user.setDepartment(department);

        // Optional password update
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        logger.info("User updated: {}", updatedUser.getEmail());

       return mapToResponse(updatedUser);
    }

    @Override
    @CacheEvict(value = "users", key = "#email")
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        logger.info("User Deleted : {}", user.getEmail());
    }

    private UserResponse mapToResponse(User user) {

        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.setRole(user.getRole().getRoleName().name());
        response.setInstitution(user.getInstitution().getName());
        response.setDepartment(user.getDepartment().getName());

        return response;
    }
}
