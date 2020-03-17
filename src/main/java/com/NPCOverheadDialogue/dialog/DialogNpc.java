/*
 * Copyright (c) 2020, TheStonedTurtle <https://github.com/TheStonedTurtle>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.NPCOverheadDialogue.dialog;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

@Getter
public enum DialogNpc
{
	RELDO("Reldo", DialogCategories.LIBRARIAN),
	CLEANER("Cleaner", DialogCategories.CLEANER),
	ROD_FISHING_SPOT("Rod Fishing spot", DialogCategories.FISHING_SPOT),
	FISHING_SPOT("Fishing spot", DialogCategories.FISHING_SPOT),
	RAT("Rat", DialogCategories.CRITTERS),
	GARGOYLE("Gargoyle", DialogCategories.UNDEAD),
	;

	private final String npcName;
	private final DialogCategories[] dialogCategories;

	DialogNpc(final String npcName, DialogCategories... dialogCategories)
	{
		this.npcName = npcName;
		this.dialogCategories = dialogCategories;
	}

	private static final Map<String, DialogNpc> NAME_MAP;
	static
	{
		ImmutableMap.Builder<String, DialogNpc> builder = new ImmutableMap.Builder<>();
		for (final DialogNpc n : values())
		{
			builder.put(n.getNpcName().toUpperCase(), n);
		}
		NAME_MAP = builder.build();
	}

	public static boolean isDialogNpc(final String npcName)
	{
		return NAME_MAP.containsKey(npcName.toUpperCase());
	}

	@Nullable
	public static DialogNpc getDialogNpcsByNpcName(final String npcName)
	{
		return NAME_MAP.get(npcName.toUpperCase());
	}

	@Nullable
	public String[] getAmbientDialogs()
	{
		String[] dialogs = new String[0];
		for (final DialogCategories category : dialogCategories)
		{
			if (category.getAmbientDialogs() != null)
			{
				dialogs = ArrayUtils.addAll(dialogs, category.getAmbientDialogs());
			}
		}

		return dialogs.length > 0 ? dialogs : null;
	}

	@Nullable
	public static String[] getAmbientDialogsByNpcName(final String npcName)
	{
		final DialogNpc v = NAME_MAP.get(npcName.toUpperCase());

		if (v == null)
		{
			return null;
		}

		return v.getAmbientDialogs();
	}

	@Nullable
	public String[] getDamageDialogs()
	{
		String[] dialogs = new String[0];
		for (final DialogCategories category : dialogCategories)
		{
			if (category.getDamageDialogs() != null)
			{
				dialogs = ArrayUtils.addAll(dialogs, category.getDamageDialogs());
			}
		}

		return dialogs.length > 0 ? dialogs : null;
	}

	@Nullable
	public static String[] getDamageDialogsByNpcName(final String npcName)
	{
		final DialogNpc v = NAME_MAP.get(npcName.toUpperCase());

		if (v == null)
		{
			return null;
		}

		return v.getDamageDialogs();
	}

	@Nullable
	public String[] getDeathDialogs()
	{
		String[] dialogs = new String[0];
		for (final DialogCategories category : dialogCategories)
		{
			if (category.getDeathDialogs() != null)
			{
				dialogs = ArrayUtils.addAll(dialogs, category.getDeathDialogs());
			}
		}

		return dialogs.length > 0 ? dialogs : null;
	}

	@Nullable
	public static String[] getDeathDialogsByNpcName(final String npcName)
	{
		final DialogNpc v = NAME_MAP.get(npcName.toUpperCase());

		if (v == null)
		{
			return null;
		}

		return v.getDeathDialogs();
	}
}
