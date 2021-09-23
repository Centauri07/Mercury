package me.centauri07.mercury.school.entity;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;

@Data
public class Student {
    @BsonProperty(value = "discord_id")
    private String discordId;
    private String name;
    private String email;
    private boolean officer = false;
    @BsonProperty(value = "birth_date")
    private Date birthDate;
}
