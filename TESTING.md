Complete test procedure
=======================

## 1. Controls Menu

1. Click **[Options]** -> **[Controls]**
1. - ✔ **[Better Sprinting]** button appears in place of **[Auto-Jump]** button
1. - ✔ **[Sprint]** keybinding is removed
1. Click **[Better Sprinting]**
1. - ✔ All option buttons and labels are aligned properly
1. - ✔ All option buttons have a tooltip
1. Click **[Disable mod functionality]**
1. - ✔ All other option buttons except for **[Sprint] (hold)**, **[Sprint menu]**, **[Auto-jump]** become disabled

## 2. In-game Vanilla

1. Basic test
1. - ✔ Pressing sprint key once begins sprint
1. - ✔ Double-tapping forward key begins sprint
1. - ✔ Mouse wheel controls spectator mode speed
1. Run `/effect give @p minecraft:blindness 5 0`
1. - ✔ Ensure sprinting is blocked
1. Run `/effect give @p minecraft:hunger 10 255`
1. - ✔ Ensure sprinting stops once hunger drops too low
1. Press the **[Sprint menu]** key
1. - ✔ Ensure clicking **[Controls]** and then **[Done]** returns to the sprint menu

## 3. In-game Custom

1. Click **[Disable mod functionality]** to re-enable the mod
1. Single key bindings
1. - ✔ Sprint (hold)
1. - ✔ Sprint (toggle)
1. - ✔ Sneak (toggle)
1. Combined key bindings
1. - ✔ While sprinting, hold *sneak (hold)* key to sneak, release to resume sprint
1. - ✔ While sprinting, press *sneak (toggle)* key to sneak, press again to resume sprint
1. - ✔ While toggle sprinting, press and release *sprint (hold)* key to cancel toggle
1. - ✔ While sneaking, both forms of sprinting are blocked
1. Ensure remaining options behave as expected
1. - ✔ Double tapping
1. - ✔ Run in all directions
1. - ✔ Flying boost
1. - ✔ Fly on ground
1. - ✔ Auto-jump
1. Test special interactions
1. - ✔ While toggle sneaking, open chat/inventory/pause menu to stop sneaking, close menu to resume sneaking
1. - ✔ While submerged in water, both forms of sprinting trigger swimming
1. Run `/effect give @p minecraft:blindness 5 0`
1. - ✔ Ensure sprinting is blocked
1. Run `/effect give @p minecraft:hunger 10 255`
1. - ✔ Ensure sprinting stops once hunger drops too low

## 4. Modded Client + Modded Server

1. Install the mod on client and server, and connect
1. - ✔ Ensure `/bettersprinting` is only available to operators
1. - ✔ The mod works and sprinting in all directions is disabled
1. Run `/bettersprinting setting runInAllDirs true`
1. - ✔ Sprinting in all directions can now be controlled in sprint menu
1. - ✔ Restarting the server remembers the setting
1. Run `/bettersprinting setting runInAllDirs false`
1. - ✔ Sprinting in all directions no longer works
1. Run `/bettersprinting disablemod true`
1. - ✔ Client receives a notification message
1. - ✔ Vanilla mechanics are forced as if **[Disable mod functionality]** was enabled
1. - ✔ Logging out and back in displays the message and keeps vanilla mechanics
1. - ✔ Restarting the server remembers the setting
1. Switch to a supported language other than English
1. Run `/bettersprinting disablemod false`
1. - ✔ Client receives a notification message (in the selected language)
1. - ✔ Modded mechanics are restored
1. Manually turn on **[Disable mod functionality]**
1. - ✔ Running the `disablemod` commands again does not show a notification message

## 5. Modded Client + Vanilla Server

1. Run vanilla server, and connect
1. - ✔ The mod works and sprinting in all directions is disabled

## 6. Vanilla Client + Modded Server

1. Run modded server, switch to vanilla client, and connect
1. Run `/bettersprinting disablemod true`
1. - ✔ Client does not receive a notification message
1. - ✔ Command response is displayed in English
