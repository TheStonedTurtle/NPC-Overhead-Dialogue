package com.NPCOverheadDialogue;

import com.google.inject.Provides;

import javax.annotation.Nullable;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import net.runelite.http.api.npc.NpcInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
        name = "NPC Overhead Dialog"
)
public class NPCOverheadDialoguePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private NPCOverheadDialogueConfig config;

    @Inject
    ClientThread clientThread;

    @Inject
    NPCManager npcManager;

    private Actor actor;
    private String lastNPCText = "";
    private String lastPlayerText = "";
    private ArrayList<NPCWithTicks> NPCList = new ArrayList<>();
    private int trackingTick = 0;


    @Override
    protected void startUp() throws Exception {
        log.info("Example started!");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Example stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
        }

    }

    @Provides
    NPCOverheadDialogueConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NPCOverheadDialogueConfig.class);
    }


    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged) {
        //log.info("animation changed for: " + animationChanged.getActor().getName());
        NPC npc = null;
        if (animationChanged.getActor() instanceof NPC) {
            npc = (NPC) animationChanged.getActor();
        }
        if (npc != null && npc.isDead()) {
            //for death text, best option for slayer item killed monsters
            //hitsplatNPCText(animationChanged.getActor(), "Giant rat", "I am a dead giant rat");
            hitsplatNPCText(animationChanged.getActor(), "Gargoyle", "*crumbles*");
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        //log.info(event.getActor().getName() + " health is " + (event.getActor().getHealthRatio()* npcManager.getHealth(npc.getId()))/event.getActor().getHealth() + " outside the thread");
        clientThread.invokeLater(() -> {
            NPC npc = null;
            if (event.getActor() instanceof NPC) {
                npc = (NPC) event.getActor();
            }
            if (npc != null) {
                log.info(event.getActor().getName() + " health is " + (event.getActor().getHealthRatio()* npcManager.getHealth(npc.getId()))/event.getActor().getHealth() + " inside the thread");
                if (event.getHitsplat().getAmount() > 0 /*&& (event.getActor().getHealth() > 0 || event.getActor().getHealth() == -1)*/ && !npc.isDead()) {
                    //for hitsplat text
                    hitsplatNPCText(event.getActor(), "Rat", "hiss");
                    hitsplatNPCText(event.getActor(), "Giant rat", "I am a giant rat");
                    log.info("hitsplat applied on " + event.getActor().getName());
                }
                else if(npc.isDead()){
                    //for death text
                    hitsplatNPCText(event.getActor(), "Giant rat", "I am a dead giant rat");
                    hitsplatNPCText(event.getActor(), "Rat", "hissssssssssssss");
                }
            }
        });

        //for death text
        /*
        else if (event.getActor().getHealth() == 0) {
            hitsplatNPCText(event.getActor(), "Rat", "hisssssssssssssss");
            hitsplatNPCText(event.getActor(), "Giant rat", "I am a dead giant rat");
        }*/
    }

    //for hitsplat text rendering
    public void hitsplatNPCText(Actor actor, String npcName, String dialogue) {
        if (actor != null && Objects.equals(actor.getName(), npcName)) {
            int npcIndex = npcExistence((NPC) actor);
            NPCList.get(npcIndex).setNPCDialog(dialogue);
            if (NPCList.get(npcIndex).getNpcTicksSinceDialogStart() >= 2) {
                npcOverheadText(actor, dialogue);
                NPCList.get(npcIndex).setInCombat(true);
                log.info(actor.getName() + " #" + npcIndex + " dialogue is set to: " + dialogue);
                NPCList.get(npcIndex).setNPCTicksSinceDialogStart(0);
                //log.info(NPCList.get(npcIndex).getNPCName() + " : " + NPCList.get(npcIndex).getNPCID() + " : ambient ticks set to 0");
            }/*else {
                    NPCList.get(npcIndex).incrementNPCTicksSinceDialogStart();
                    //log.info(NPCList.get(npcIndex).getNPCName() + " : " + NPCList.get(npcIndex).getNPCID() + " : ticks since ambient start : " + NPCList.get(npcIndex).getNpcTicksSinceDialogStart());
                }*/
        }
    }

    //for ambient text or hitsplat text rendering
    public void ambientNPCText(Actor actor, String npcName, String dialogue) {
        if (actor != null && Objects.equals(actor.getName(), npcName)) {
            int npcIndex = npcExistence((NPC) actor);
            NPCList.get(npcIndex).setNPCDialog(dialogue);
            if (NPCList.get(npcIndex).getNpcTicksSinceDialogStart() >= 10 && (int) (Math.random() * ((10 - 1) + 1)) > 5) {
                npcOverheadText(actor, dialogue);
                NPCList.get(npcIndex).setNPCTicksSinceDialogStart(0);
                //log.info(NPCList.get(npcIndex).getNPCName() + " : " + NPCList.get(npcIndex).getNPCID() + " : ambient ticks set to 0");
            } else {
                NPCList.get(npcIndex).incrementNPCTicksSinceDialogStart();
                //log.info(NPCList.get(npcIndex).getNPCName() + " : " + NPCList.get(npcIndex).getNPCID() + " : ticks since ambient start : " + NPCList.get(npcIndex).getNpcTicksSinceDialogStart());
            }
        }
    }


    //runs every game tick
    //checks all local NPCs for movement overhead text and applies if necessary
    //for ambient and walking text
    public void npcTextInvoker() {
        //For when NPCs are moving
        List<NPC> localNPCs = client.getNpcs();
        for (NPC localNPC : localNPCs) {
            //ambient texts
            ambientNPCText(localNPC, "Rod Fishing spot", "*blub* *blub*");
            //walking texts
            npcWalkingText(localNPC, "Reldo", "I am a librarian");
            npcWalkingText(localNPC, "Cleaner", "*Sweep* *Sweep*");
        }
    }

    //checks if the NPC exists in NPCList, then adds it if it doesn't
    public int npcExistence(NPC npc) {
        boolean npcExists = false;
        int npcIndex = 0;

        if (NPCList.size() > 0) {
            for (NPCWithTicks n : NPCList) {
                if (n.getNPCWithTicksNPC() == npc) {
                    npcExists = true;
                    break;
                }
                npcIndex++;
            }
        }

        if (!npcExists) {
            NPCList.add(new NPCWithTicks(npc.getName(), npc.getId(), npc, npc, client.getTickCount(), npc.getWorldLocation().getX(), npc.getWorldLocation().getY()));
        }
        return npcIndex;
    }

    //for walking text rendering
    public void npcWalkingText(NPC npc, String npcName, String dialogue) {
        if (npc != null && Objects.equals(npc.getName(), npcName)) {
            WorldPoint npcPos = npc.getWorldLocation();
            int currentTick = client.getTickCount(); //for debugging
            int npcIndex = npcExistence(npc);
            if (npc.getOverheadText() != null) {
                NPCList.get(npcIndex).incrementNPCTicksSinceDialogStart();
            }

            if (npcPos.getX() != NPCList.get(npcIndex).getLastXCoordinate() || npcPos.getY() != NPCList.get(npcIndex).getLastYCoordinate() && NPCList.get(npcIndex).getNPCTicksWithoutMoving() >= 2) {
                log.info("Game tick: " + currentTick + " : " + npc.getName() + " moved: " + npcPos.getX() + " " + npcPos.getY());
                //NPCList.get(npcIndex).setMovedLastGameTick(true);
                NPCList.get(npcIndex).setNPCTicksWithoutMoving(0);
                NPCList.get(npcIndex).setNPCTicksSinceDialogStart(0);
                npcOverheadText(npc, dialogue);
                NPCList.get(npcIndex).setNPCDialog(dialogue);
                //NPCList.get(npcIndex).setNPCStartingTick(client.getTickCount());
            } else {
                log.info("Game tick: " + currentTick + " : " + npc.getName() + " has stopped moving");
                //NPCList.get(npcIndex).setMovedLastGameTick(false);
                NPCList.get(npcIndex).incrementNPCTicksWithoutMoving();
            }
            NPCList.get(npcIndex).setLastXCoordinate(npcPos.getX());
            NPCList.get(npcIndex).setLastYCoordinate(npcPos.getY());
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (event.getTarget() != null && event.getSource() == client.getLocalPlayer()) {
            lastNPCText = null;
            lastPlayerText = null;
            actor = event.getTarget();
        }
    }

    //runs every game tick
    //checks if there is NPC or player dialog
    public void npcDialog() {
        if (client.getWidget(WidgetInfo.DIALOG_NPC_TEXT) != null && !lastNPCText.equals(Text.sanitizeMultilineText((client.getWidget(WidgetInfo.DIALOG_NPC_TEXT)).getText()))) {
            Widget npcDialog = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);
            if (npcDialog != null) {
                String npcText = Text.sanitizeMultilineText(npcDialog.getText());
                lastNPCText = npcText;
                log.info(npcText);
                npcOverheadText(actor, npcText);
            }
        }
        //For when your player has dialogue
        if (client.getWidget(WidgetID.DIALOG_PLAYER_GROUP_ID, 4) != null && !lastPlayerText.equals(Text.sanitizeMultilineText((client.getWidget(WidgetID.DIALOG_PLAYER_GROUP_ID, 4)).getText()))) {
            Widget playerDialog = client.getWidget(WidgetID.DIALOG_PLAYER_GROUP_ID, 4);
            if (playerDialog != null) {
                String playerText = Text.sanitizeMultilineText(playerDialog.getText());
                lastPlayerText = playerText;
                log.info(playerText);
                npcOverheadText(Objects.requireNonNull(client.getLocalPlayer()), playerText);
            }
        }
    }

    //sets the overhead text
    public void npcOverheadText(Actor a, String dialogue) {
        a.setOverheadText(dialogue);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        trackingTick++;
        npcDialog();
        npcTextInvoker();
        for (int i = 0; i < NPCList.size(); i++) {
            if (NPCList.get(i).getInCombat()) {
                if (NPCList.get(i).getNPCDialog() != null && NPCList.get(i).getNpcTicksSinceDialogStart() >= 2) {
                    NPCList.get(i).getNPCWithTicksActor().setOverheadText(null);
                    NPCList.get(i).setNPCDialog(null);
                    NPCList.get(i).setInCombat(false);
                    log.info(NPCList.get(i).getNPCName() + " #" + i + " overhead text removed due to 2 ticks");
                } else {
                    NPCList.get(i).getNPCWithTicksActor().setOverheadText(NPCList.get(i).getNPCDialog());
                    NPCList.get(i).incrementNPCTicksSinceDialogStart();
                    //log.info(NPCList.get(i).getNPCName() + " #" + i + " overhead text refreshed with " + NPCList.get(i).getNpcTicksSinceDialogStart() + " ticks");
                }
            } else {
                if (NPCList.get(i).getNPCDialog() != null && NPCList.get(i).getNpcTicksSinceDialogStart() >= 5) {
                    NPCList.get(i).getNPCWithTicksActor().setOverheadText(null);
                    NPCList.get(i).setNPCDialog(null);
                    log.info(NPCList.get(i).getNPCName() + " #" + i + " overhead text removed due to 5 ticks");
                } else {
                    NPCList.get(i).getNPCWithTicksActor().setOverheadText(NPCList.get(i).getNPCDialog());
                    //log.info(NPCList.get(i).getNPCName() + " #" + i + " overhead text refreshed with " + NPCList.get(i).getNpcTicksSinceDialogStart() + " ticks");
                }
            }
        }
    }
}