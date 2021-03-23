package pl.devims.util;

import pl.devims.dto.SocialUserDto;
import pl.devims.entity.User;

public class SocialMediaUtils {

    public static User parseFromDtoToUser(SocialUserDto socialUserDto) {
        return User.builder()
                .email(socialUserDto.getUserMail())
                .firstName(socialUserDto.getFirstName())
                .lastName(socialUserDto.getLastName())
                .photoUrl(socialUserDto.getPhotoUrl())
                .socialServiceName(socialUserDto.getSocialServiceName())
                .build();
    }
}
