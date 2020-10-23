package com.disappointedduck.utility;

import com.disappointedduck.model.RoleEnum;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;

@UtilityClass
public class CommonUtilities {
    public void addWhiteCheckmark(Message message) {
        message.addReaction("U+2705").queue();
    }

    public void addRedCross(Message message) {
        message.addReaction("U+274C").queue();
    }

    public void addEyes(Message message) {
        message.addReaction("U+1F440").queue();
    }

    public void addFire(Message message) {
        message.addReaction(RoleEnum.DAMAGE_DEALER.getEmote()).queue();
    }

    public void addShield(Message message) {
        message.addReaction(RoleEnum.TANK.getEmote()).queue();
    }

    public void addGreenHeart(Message message) {
        message.addReaction(RoleEnum.HEALER.getEmote()).queue();
    }


}
