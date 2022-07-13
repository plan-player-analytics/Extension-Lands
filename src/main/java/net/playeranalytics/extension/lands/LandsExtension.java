/*
    Copyright(c) 2021 AuroraLS3

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package net.playeranalytics.extension.lands;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.NotReadyException;
import com.djrapitops.plan.extension.annotation.DataBuilderProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.builder.ExtensionDataBuilder;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

/**
 * DataExtension.
 *
 * @author AuroraLS3
 */
@PluginInfo(name = "Lands", iconName = "umbrella-beach", iconFamily = Family.SOLID, color = Color.TEAL)
public class LandsExtension implements DataExtension {

    private final LandsIntegration lands;

    public LandsExtension() {
        Plugin plan = Bukkit.getPluginManager().getPlugin("Plan");
        if (plan == null) throw new NotReadyException(); // What are the odds
        lands = new LandsIntegration(plan);
    }

    public LandsExtension(boolean forTesting) {
        lands = null;
    }

    @DataBuilderProvider
    public ExtensionDataBuilder playerData(UUID playerUUID) {
        LandPlayer landPlayer = lands.getLandPlayer(playerUUID);
        if (landPlayer == null) throw new NotReadyException();

        long totalLandSize = 0;
        long totalChunksAvailable = 0;

        Table.Factory table = Table.builder()
                .columnOne("Land", Icon.called("umbrella-beach").build())
                .columnTwo("Size", Icon.called("expand-arrows-alt").build())
                .columnThree("Spawn", Icon.called("map-pin").build());

        Set<? extends Land> playerLands = landPlayer.getLands();
        for (Land land : playerLands) {
            int size = land.getSize();
            totalLandSize += size;
            Location spawn = land.getSpawn();
            int maxChunks = land.getMaxChunks(false);
            totalChunksAvailable += maxChunks;
            table.addRow(land.getName(), size + " / " + maxChunks, spawn != null ? "x:" + spawn.getBlockX() + " z:" + spawn.getBlockZ() : "unset");
        }

        return newExtensionDataBuilder()
                .addTable("lands", table.build(), Color.TEAL)
                .addValue(String.class, valueBuilder("Claimed chunks")
                        .icon(Icon.called("expand-arrows-alt").of(Color.TEAL).build())
                        .buildString(totalLandSize + " / " + totalChunksAvailable));
    }
}