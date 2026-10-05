package com.livingrelics.engine.entity_extensions;

import com.google.gson.JsonObject;

public class EntityExtension {
    private final JsonObject data;
    public EntityExtension(JsonObject data){
        this.data = data;
    }

    public String getEntity(){
        return data
                .get("entity")
                .getAsString();
    }

    public String getType(){
        if(!data.has("type")){
            return "NORMAL";
        }
        return data.get("type").getAsString();
    }

    public boolean hasBossBar(){
        return data.has("boss_bar")
            && data
                .getAsJsonObject("boss_bar")
                .get("enabled")
                .getAsBoolean();
    }

    public String getBossBarName(){
        return data
                .getAsJsonObject("boss_bar")
                .get("name")
                .getAsString();
    }

    public String getBossBarNameColor(){
        return data
            .getAsJsonObject("boss_bar")
            .get("name_color")
            .getAsString();
    }

    public String getBossBarStyle(){
        return data
                .getAsJsonObject("boss_bar")
                .get("style")
                .getAsString();
    }


    public boolean hasLoot(){
        return data.has("loot");
    }

    public JsonObject getLoot(){
        if(!data.has("loot")){
            return null;
        }
        return data.getAsJsonObject("loot");
    }

}