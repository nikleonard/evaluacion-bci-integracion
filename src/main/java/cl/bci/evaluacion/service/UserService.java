package cl.bci.evaluacion.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import cl.bci.evaluacion.exception.DuplicateEmailException;
import cl.bci.evaluacion.model.dto.PhoneDTO;
import cl.bci.evaluacion.model.dto.UserRequestDTO;
import cl.bci.evaluacion.model.dto.UserResponseDTO;
import cl.bci.evaluacion.model.entity.Phone;
import cl.bci.evaluacion.model.entity.User;
import cl.bci.evaluacion.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO registerUser(UserRequestDTO request) {
        // Chequear email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("El correo ya registrado");
        }

        // generar token
        // TODO: generar token JWT
        String token = UUID.randomUUID().toString();

        // Crear usuario
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .token(token)
                .phones(request.getPhones().stream()
                        .map(phoneDTO -> Phone.builder()
                                .number(phoneDTO.getNumber())
                                .citycode(phoneDTO.getCitycode())
                                .countrycode(phoneDTO.getCountrycode())
                                .build())
                        .toList())
                .build();

        // Añadir usuario a los telefonos
        user.getPhones().forEach(phone -> phone.setUser(user));

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isActive(user.isActive())
                .phones(user.getPhones().stream()
                        .map(phone -> PhoneDTO.builder()
                                .number(phone.getNumber())
                                .citycode(phone.getCitycode())
                                .countrycode(phone.getCountrycode())
                                .build())
                        .toList())
                .build();
    }
}
