package com.jennyqi.jukebox.model;

import java.util.List;

public record Jukebox(String id, String model, List<Component> components) {}
