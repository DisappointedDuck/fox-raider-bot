package com.disappointedduck.utility;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Value;

@UtilityClass
public class CommonProperties {
    public static String CHANNEL;

    public static String STORE;

    public static Guild GUILD;
}
