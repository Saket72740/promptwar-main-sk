import re

filepath = "src/main/java/com/examstress/Wellness/AISimulatorService.java"
with open(filepath, "r") as f:
    content = f.read()

# Replace Map.of with a LinkedHashMap to preserve iteration order
# We will use an initialization block or something similar, or just keep Map.of but acknowledge it's fine for now, or change to Map.ofEntries with LinkedHashMap?
# Actually, since Map.of in Java 9+ iteration order is randomized, we can create a LinkedHashMap.
# Let's write a replacement block.
