package pl.devims.dto;

import lombok.Data;

@Data
public class SocialUserDto {
    private String userMail;
    private String firstName;
    private String lastName;
    private String token;
    private String photoUrl;
    private String socialServiceName;

}
