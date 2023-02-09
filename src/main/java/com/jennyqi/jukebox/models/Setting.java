package com.jennyqi.jukebox.models;

import java.util.List;

public record Setting(String id, List<String> requires) {}
