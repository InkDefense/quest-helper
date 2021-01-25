/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.entertheabyss;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.SkillRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.ENTER_THE_ABYSS
)
public class EnterTheAbyss extends BasicQuestHelper
{
	// Recommended
	ItemRequirement varrockTeleport, ardougneTeleport, edgevilleTeleport, passageTeleport;

	// Items during quest
	ItemRequirement scryingOrb, scryingOrbCharged;

	ConditionForStep inWizardBasement, teleportedFromVarrock, teleportedFromArdougne, teleportedFromWizardsTower,
		teleportedFromGnome, teleportedFromDistentor, chargedScryingOrb;

	QuestStep talkToMageInWildy, talkToMageInVarrock, talkToAubury, goDownInWizardsTower, talkToSedridor,
		talkToCromperty, talkToMageAfterTeleports, talkToMageToFinish;

	Zone wizardBasement;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMageInWildy);
		steps.put(1, talkToMageInVarrock);

		ConditionalStep locateEssenceMine = new ConditionalStep(this, talkToAubury);
		locateEssenceMine.addStep(new Conditions(chargedScryingOrb), talkToMageAfterTeleports);
		locateEssenceMine.addStep(new Conditions(teleportedFromVarrock, teleportedFromWizardsTower), talkToCromperty);
		locateEssenceMine.addStep(new Conditions(teleportedFromVarrock, inWizardBasement), talkToSedridor);
		locateEssenceMine.addStep(teleportedFromVarrock, goDownInWizardsTower);
		steps.put(2, locateEssenceMine);

		steps.put(3, talkToMageToFinish);

		return steps;
	}

	public void setupRequirements()
	{
		varrockTeleport = new ItemRequirement("Teleports to Varrock", ItemID.VARROCK_TELEPORT, 2);
		ardougneTeleport = new ItemRequirement("Teleport to Ardougne", ItemID.ARDOUGNE_TELEPORT);
		edgevilleTeleport = new ItemRequirement("Teleport to Edgeville", ItemCollections.getAmuletOfGlories());
		passageTeleport = new ItemRequirement("Teleport to Wizards' Tower", ItemCollections.getNecklaceOfPassages());

		scryingOrb = new ItemRequirement("Scrying orb", ItemID.SCRYING_ORB_5519);
		scryingOrb.setTip("You can get another from the Mage of Zamorak in south east Varrock");

		scryingOrbCharged = new ItemRequirement("Scrying orb", ItemID.SCRYING_ORB);
		scryingOrbCharged.setTip("You can get another from the Mage of Zamorak in south east Varrock");
	}

	public void loadZones()
	{
		wizardBasement = new Zone(new WorldPoint(3094, 9553, 0), new WorldPoint(3125, 9582, 0));
	}

	public void setupConditions()
	{
		inWizardBasement = new ZoneCondition(wizardBasement);

		teleportedFromWizardsTower = new VarbitCondition(2314, 1);
		teleportedFromVarrock = new VarbitCondition(2315, 1);
		teleportedFromArdougne = new VarbitCondition(2316, 1);
		teleportedFromDistentor = new VarbitCondition(2317, 1);
		teleportedFromGnome = new VarbitCondition(2318, 1);

		chargedScryingOrb = new ItemRequirementCondition(scryingOrbCharged);
	}

	public void setupSteps()
	{
		talkToMageInWildy = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK, new WorldPoint(3102, 3557, 0), "Talk to the Mage" +
			" of Zamorak in the Wilderness north of Edgeville. BRING NOTHING AS YOU CAN BE KILLED BY OTHER PLAYERS HERE.");

		talkToMageInVarrock = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3259, 3383, 0),
			"Talk to the Mage of Zamorak in south east Varrock.");
		talkToMageInVarrock.addDialogSteps("Where do you get your runes from?", "Yes");

		talkToAubury = new NpcStep(this, NpcID.AUBURY, new WorldPoint(3253, 3401, 0),
			"Teleport to the essence mine with Aubury in south east Varrock.", scryingOrb);
		talkToAubury.addDialogStep("Can you teleport me to the Rune Essence?");

		goDownInWizardsTower = new ObjectStep(this, ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"Teleport to the essence mine with Sedridor in the Wizard Tower's basement.", scryingOrb);
		goDownInWizardsTower.addDialogStep("Wizard's Tower");
		talkToSedridor = new NpcStep(this, NpcID.SEDRIDOR, new WorldPoint(3104, 9571, 0),
			"Teleport to the essence mine with Sedridor in the Wizard Tower's basement.", scryingOrb);
		talkToSedridor.addDialogStep("Can you teleport me to the Rune Essence?");
		talkToSedridor.addSubSteps(goDownInWizardsTower);

		talkToCromperty = new NpcStep(this, NpcID.WIZARD_CROMPERTY, new WorldPoint(2684, 3323, 0),
			"Teleport to the essence mine with Wizard Cromperty in East Ardougne.", scryingOrb);
		talkToCromperty.addDialogStep("Can you teleport me to the Rune Essence?");

		talkToMageAfterTeleports = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3259, 3383, 0),
			"Talk to the Mage of Zamorak in south east Varrock.", scryingOrbCharged);
		talkToMageToFinish = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3259, 3383, 0),
			"Talk to the Mage of Zamorak again.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(edgevilleTeleport, varrockTeleport, passageTeleport, ardougneTeleport));
	}

	@Override
	public ArrayList<String> getNotes()
	{
		return new ArrayList<>(Collections.singletonList("The start of this miniquest is in the Wilderness. Other players can " +
			"attack you there, so make sure to not bring anything there!"));
	}

	@Override
	public ArrayList<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED));
		return req;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Helping the Zamorakians",
			new ArrayList<>(Arrays.asList(talkToMageInWildy, talkToMageInVarrock, talkToAubury, talkToSedridor,
				talkToCromperty, talkToMageAfterTeleports, talkToMageToFinish))));

		return allSteps;
	}
}