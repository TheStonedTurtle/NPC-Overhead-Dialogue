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

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DialogCategories
{
	LIBRARIAN(
		new String[]{"I am a librarian", "shhhhhh", "I have always imagined that paradise will be a kind of Library"},
		null, null),
	STORE_CLERK(
		new String[]{"Come check out my wares!", "I have the best prices around", "Buying all junk", "Bank sale"},
		null, null),
	CLEANER(
		new String[]{"*Sweep* *Sweep*", "Time for a break!", "I just swept there!"},
		null, null),
	FISHING_SPOT(
		new String[]{"*blub* *blub*", "*splash*"},
		null, null),
	CRITTERS(
		new String[]{"*scamper* *scamper*"},
		new String[]{"hiss"},
		new String[]{"hissssssssssssss"}),
	UNDEAD(
		null,
		null,
		new String[]{"*crubmles*"}
	)
	;

	private final String[] ambientDialogs;
	private final String[] damageDialogs;
	private final String[] deathDialogs;
}
