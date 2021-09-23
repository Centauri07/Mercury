package me.centauri07.mercury.school.section;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class SectionInformation {
    private String name;
    @BsonProperty(value = "guild_id")
    private String guildId;
    @BsonProperty(value = "announcement_channel_id")
    private String announcementChannelId;
    @BsonProperty(value = "tasks_channel_id")
    private String tasksChannelId;
    @BsonProperty(value = "role_id")
    private String roleId;
}
