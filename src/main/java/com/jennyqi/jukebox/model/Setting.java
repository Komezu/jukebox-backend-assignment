package com.jennyqi.jukebox.model;

import java.util.List;

public record Setting(String id, List<String> requires) {}
