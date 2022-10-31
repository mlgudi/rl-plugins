package com.typetohop;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.http.api.worlds.World;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.WorldResult;

@Slf4j
@PluginDescriptor(
		name = "Type to Hop"
)
public class TypeToHopPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private WorldService worldService;

	private int world;
	private net.runelite.api.World quickHopTargetWorld;

	@Subscribe
	public void onScriptPreFired(ScriptPreFired scriptPreFired)
	{
		if (scriptPreFired.getScriptId() != 1984) { return; }

		String msg = client.getVarcStrValue(VarClientStr.CHATBOX_TYPED_TEXT);
		if (!msg.startsWith("::") || msg.length() != 5) { return; }

		String worldStr = msg.substring(2, 5);
		try {
			world = Integer.parseInt(worldStr);
			hop(world);
		}
		catch (NumberFormatException ignored) {}
	}

	private void hop(int worldId)
	{
		WorldResult worldResult = worldService.getWorlds();
		World world = worldResult.findWorld(worldId);
		if (world == null)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("Invalid world number: %d", worldId), null);
			return;
		}

		final net.runelite.api.World rsWorld = client.createWorld();
		rsWorld.setActivity(world.getActivity());
		rsWorld.setAddress(world.getAddress());
		rsWorld.setId(world.getId());
		rsWorld.setPlayerCount(world.getPlayers());
		rsWorld.setLocation(world.getLocation());
		rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("Attempting to hop to world %d...", worldId), null);
		quickHopTargetWorld = rsWorld;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (quickHopTargetWorld == null)
		{
			return;
		}

		if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null)
		{
			client.openWorldHopper();
		}
		else
		{
			client.hopToWorld(quickHopTargetWorld);
			quickHopTargetWorld = null;
			world = -1;
		}
	}
}
