package net.elarisrpg.classsystem;

public enum PlayerClass {
        NONE,
        WARRIOR,
        MAGE,
        ROGUE;

        public static PlayerClass fromString(String str) {
            for (PlayerClass value : values()) {
                if (value.name().equalsIgnoreCase(str)) {
                    return value;
                }
            }
            return NONE;
        }
}