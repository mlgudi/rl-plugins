package com.typetohop;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TypeToHopPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TypeToHopPlugin.class);
		RuneLite.main(args);
	}
}