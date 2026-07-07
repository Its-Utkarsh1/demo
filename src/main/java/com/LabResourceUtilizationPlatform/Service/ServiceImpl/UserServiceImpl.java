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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private InstitutionRepository institutionRepository;
    private DepartmentRepository departmentRepository;
    private ModelMapper modelMapper;


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
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found."));

        //Finding either Institution exists or not
        Institution institution = institutionRepository.findById(request.getInstitutionId())
                .orElseThrow(()->new RuntimeException("Institution not found."));

        //Finding either Department exists or not
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(()->new RuntimeException("Department not found."));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .registrationId(request.getRegistrationId())
                .role(role)
                .institution(institution)
                .department(department)
                .build();

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser,UserResponse.class);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found."));

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
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Institution institution = institutionRepository.findById(request.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        modelMapper.map(request,User.class);
        user.setRole(role);
        user.setInstitution(institution);
        user.setDepartment(department);

        User updatedUser = userRepository.save(user);
        UserResponse userResponse = modelMapper.map(updatedUser,UserResponse.class);
        userResponse.setRole(updatedUser.getRole().getRoleName().name());
        userResponse.setDepartment(updatedUser.getDepartment().getName());
        userResponse.setInstitution(updatedUser.getInstitution().getName());

        return userResponse;
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}
