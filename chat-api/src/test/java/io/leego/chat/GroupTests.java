package io.leego.chat;

import io.leego.chat.constant.MemberStatus;
import io.leego.chat.entity.GroupMember;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@Rollback
@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GroupTests extends ChatTests {
    private static final Logger logger = LoggerFactory.getLogger(GroupTests.class);

    @Test
    public void testCreateMemberCache() {
        groupMemberRepository.findAll().stream()
                .filter(o -> o.getStatus() == MemberStatus.JOINED.getCode())
                .collect(Collectors.groupingBy(
                        GroupMember::getGroupId,
                        Collectors.mapping(GroupMember::getUserId, Collectors.toSet())))
                .forEach((groupId, userIds) -> groupManager.addMembers(groupId, userIds));
    }

}
