// File: TeamData.java
package net.wordsaw.chivivorsmod.team;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.PersistentState;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TeamData extends PersistentState {
    public static class Team {
        public final String name;
        private UUID leader;
        public final Set<UUID> subleaders = new HashSet<>();
        public final Set<UUID> members = new HashSet<>();
        public final Set<UUID> joinRequests = new HashSet<>();

        public boolean friendlyFire = false;
        public boolean requiresApproval = true;
        public boolean anyMemberCanApprove = false;

        public Team(String name, UUID leader) {
            this.name = name;
            this.leader = leader;
            this.members.add(leader);
        }

        public boolean isLeader(UUID uuid) {
            return leader.equals(uuid);
        }

        public boolean isSubleader(UUID uuid) {
            return subleaders.contains(uuid);
        }

        public void setLeader(UUID newLeader){
            leader = newLeader;
        }

        public UUID getLeader(){
            return leader;
        }
    }

    private static final String CHIVIVORS_TEAM_NBT_KEY = "chivivorsmod_team_data";
    public final Map<String, Team> teams = new HashMap<>();

    public static TeamData get(ServerWorld world) {
        return world.getServer().getOverworld().getPersistentStateManager()
                .getOrCreate(TeamData::fromNbt, TeamData::new, CHIVIVORS_TEAM_NBT_KEY);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        System.out.println("Saving TeamData to NBT");
        NbtCompound teamsNbt = new NbtCompound();

        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            String teamName = entry.getKey();
            Team team = entry.getValue();

            NbtCompound teamNbt = new NbtCompound();
            teamNbt.putUuid("Leader", team.getLeader());
            teamNbt.put("Subleaders", serializeUuidSet(team.subleaders));
            teamNbt.put("Members", serializeUuidSet(team.members));
            teamNbt.put("JoinRequests", serializeUuidSet(team.joinRequests));

            teamNbt.putBoolean("FriendlyFire", team.friendlyFire);
            teamNbt.putBoolean("RequiresApproval", team.requiresApproval);
            teamNbt.putBoolean("AnyMemberCanApprove", team.anyMemberCanApprove);

            teamsNbt.put(teamName, teamNbt);
        }

        nbt.put("chivivorsmod_team_data", teamsNbt);
        return nbt;
    }

    private NbtList serializeUuidSet(Set<UUID> uuids) {
        NbtList list = new NbtList();
        for (UUID uuid : uuids) {
            NbtCompound entry = new NbtCompound();
            entry.putUuid("UUID", uuid);
            list.add(entry);
        }
        return list;
    }

    public static TeamData fromNbt(NbtCompound nbt) {
        TeamData data = new TeamData();

        NbtCompound teamsNbt = nbt.getCompound("chivivorsmod_team_data");

        for (String teamName : teamsNbt.getKeys()) {
            NbtCompound teamNbt = teamsNbt.getCompound(teamName);
            UUID leader = teamNbt.getUuid("Leader");
            Team team = new Team(teamName, leader);

            team.subleaders.addAll(deserializeUuidSet(teamNbt.getList("Subleaders", NbtCompound.COMPOUND_TYPE)));
            team.members.addAll(deserializeUuidSet(teamNbt.getList("Members", NbtCompound.COMPOUND_TYPE)));
            team.joinRequests.addAll(deserializeUuidSet(teamNbt.getList("JoinRequests", NbtCompound.COMPOUND_TYPE)));

            team.friendlyFire = teamNbt.getBoolean("FriendlyFire");
            team.requiresApproval = teamNbt.getBoolean("RequiresApproval");
            team.anyMemberCanApprove = teamNbt.getBoolean("AnyMemberCanApprove");

            data.teams.put(teamName, team);
        }

        return data;
    }

    private static Set<UUID> deserializeUuidSet(NbtList list) {
        Set<UUID> uuids = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            uuids.add(entry.getUuid("UUID"));
        }
        return uuids;
    }

    public void sendGeneralMessage(MinecraftServer server, Team team, String message){
        for (UUID member : team.members){
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(member);
            if (player == null) continue;
            player.sendMessage(Text.literal(message));
        }
    }

    public void sendGeneralMessage(MinecraftServer server, UUID source, Team team, String message){
        for (UUID member : team.members){
            if (member.equals(source)) continue;
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(member);
            if (player == null) continue;
            player.sendMessage(Text.literal(message));
        }
    }

    public void sendMessageToTeamChat(MinecraftServer server, Team team, UUID sourceUuid, String message){
        for (UUID member : team.members){
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(member);
            if (player == null) continue;
            String playerName = Objects.requireNonNull(server.getPlayerManager().getPlayer(sourceUuid)).getName().getString();
            Text chatText = Text.literal("<" + playerName + "> " + message);
            player.sendMessage(chatText);
        }
    }

    public Team getPlayerTeam(UUID playerId) {
        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            Team team = entry.getValue();
            if (team.members.contains(playerId))
                return team;
        }
        return null;
    }

    public Team getTeam(String name) {
        return teams.get(name);
    }

    public String getMemberRank(UUID playerId){
        Team team = getPlayerTeam(playerId);
        if (team.isLeader(playerId))
            return "Leader";
        if (team.isSubleader(playerId))
            return "Subleader";
        return "Member";
    }

    public void createTeam(String name, ServerPlayerEntity creator) {
        if (getPlayerTeam(creator.getUuid()) != null){
            creator.sendMessage(Text.literal("You are already in a team"));
            return;
        }

        if (teams.containsKey(name)){
            creator.sendMessage(Text.literal("A team with that name already exist"));
            return;
        }

        Team team = new Team(name, creator.getUuid());
        teams.put(name, team);
        creator.sendMessage(Text.literal("Team " + name + " has been created."));
        markDirty();
    }

    public void leaveTeam(ServerPlayerEntity sourcePlayer) {
        UUID sourceUuid = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceUuid);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }

        if (team.isLeader(sourceUuid)) {
            team.members.remove(sourceUuid);

            if (!team.subleaders.isEmpty())
                team.setLeader(team.subleaders.iterator().next());
            else if (!team.members.isEmpty())
                team.setLeader(team.members.iterator().next());
            else
                teams.remove(team.name);
        }

        team.subleaders.remove(sourceUuid);
        team.joinRequests.remove(sourceUuid);
        sourcePlayer.sendMessage(Text.literal("You have left the team."));
        String message = sourcePlayer.getName() + " leaved the team";
        sendGeneralMessage(sourcePlayer.getServer(), team, message);
        markDirty();
    }

    public void joinTeam(ServerPlayerEntity sourcePlayer, String name) {
        UUID sourceUuid = sourcePlayer.getUuid();
        Team playerTeam = getPlayerTeam(sourceUuid);
        if (playerTeam != null){
            sourcePlayer.sendMessage(Text.literal("You are already in a team"));
            return;
        }

        Team targetTeam = teams.get(name);
        if (targetTeam == null){
            sourcePlayer.sendMessage(Text.literal("That team does not exist"));
            return;
        }

        if (targetTeam.requiresApproval) {
            targetTeam.joinRequests.add(sourceUuid);
            sourcePlayer.sendMessage(Text.literal("Sent a join request, you may join when approved"));
            return;
        }
        if (targetTeam.members.size() >= 100){
            sourcePlayer.sendMessage(Text.literal("That team is full"));
            return;
        }

        String message = sourcePlayer.getName() + " has joined the team";
        sendGeneralMessage(sourcePlayer.getServer(), targetTeam, message);
        targetTeam.members.add(sourceUuid);
        targetTeam.joinRequests.remove(sourceUuid);
        sourcePlayer.sendMessage(Text.literal("You have joined the team: " + name));
        markDirty();
    }

    public void rejectJoinRequest(ServerPlayerEntity sourcePlayer, String playerName){
        UUID sourceUuid = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceUuid);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }

        if (!team.isLeader(sourceUuid) && !team.isSubleader(sourceUuid)){
            sourcePlayer.sendMessage(Text.literal("You don't have the required permission to do that"));
            return;
        }

        ServerPlayerEntity player = Objects.requireNonNull(sourcePlayer.getServer()).getPlayerManager().getPlayer(playerName);
        if (player == null) {
            sourcePlayer.sendMessage(Text.literal("Player not found"));
            return;
        }

        UUID playerUuid = player.getUuid();
        team.joinRequests.remove(playerUuid);
        player.sendMessage(Text.literal("You have been rejected from " + team.name));
        sourcePlayer.sendMessage(Text.literal("Rejected join request from " + playerName));
        markDirty();
    }

    public void rejectAllJoinRequests(ServerPlayerEntity sourcePlayer){
        UUID sourceUuid = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceUuid);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }

        if (team.joinRequests.isEmpty()) {
            sourcePlayer.sendMessage(Text.literal("No request found"));
            return;
        }

        for (UUID request : team.joinRequests){
            ServerPlayerEntity player = Objects.requireNonNull(sourcePlayer.getServer()).getPlayerManager().getPlayer(request);
            if (player == null) continue;
            player.sendMessage(Text.literal("You have been rejected from " + team.name));
        }
        team.joinRequests.clear();
        sourcePlayer.sendMessage(Text.literal("Successfully cleared all join requests"));
        markDirty();
    }

    public void setFriendlyFire(ServerPlayerEntity sourcePlayer, boolean friendlyFire) {
        UUID sourceUuid = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceUuid);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }

        if (team.isLeader(sourceUuid) || team.isSubleader(sourceUuid)) {
            team.friendlyFire = friendlyFire;
            markDirty();
            if (friendlyFire)
                sendGeneralMessage(sourcePlayer.getServer(), team, "Friendly fire has been enabled!");
            else
                sendGeneralMessage(sourcePlayer.getServer(), team, "Friendly fire has been disabled!");
            return;
        }
        sourcePlayer.sendMessage(Text.literal("You don't have the required permission to do that"));
    }

    public void setRequiresApproval(ServerPlayerEntity sourcePlayer, boolean value) {
        UUID sourceUuid = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceUuid);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }

        if (team.isLeader(sourceUuid) || team.isSubleader(sourceUuid)) {
            team.requiresApproval = value;
            markDirty();
            if (value)
                sendGeneralMessage(sourcePlayer.getServer(), team, "The requirement for approval to join the team is enabled.");
            else
                sendGeneralMessage(sourcePlayer.getServer(), team, "The requirement for approval to join the team is disabled.");
            return;
        }
        sourcePlayer.sendMessage(Text.literal("You don't have the required permission to do that"));
    }

    public void setAnyMemberCanApprove(ServerPlayerEntity sourcePlayer, boolean value) {
        UUID sourceUuid = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceUuid);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }

        if (team.isLeader(sourceUuid) || team.isSubleader(sourceUuid)) {
            team.anyMemberCanApprove = value;
            markDirty();
            if (value)
                sendGeneralMessage(sourcePlayer.getServer(), team, "Any member of the team can approve new members now!");
            else
                sendGeneralMessage(sourcePlayer.getServer(), team, "Only leaders and subleaders can approve new members now!");
            return;
        }
        sourcePlayer.sendMessage(Text.literal("You don't have the required permission to do that"));
    }

    public void listMembers(ServerPlayerEntity sourcePlayer) {
        UUID sourceUuid = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceUuid);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }

        MinecraftServer server = sourcePlayer.getServer();
        if (server == null) return;

        sourcePlayer.sendMessage(Text.literal("Team members:"));
        for (UUID uuid : team.members) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                sourcePlayer.sendMessage(Text.literal
                        ("- " + player.getName().getString() + " (" + getMemberRank(player.getUuid()) + ")"), false);
            } else {
                sourcePlayer.sendMessage(Text.literal("- Unknown Player (" + uuid + ")" + " (" + getMemberRank(uuid) + ")"), false);
            }
        }
    }

    public void listMembers(ServerPlayerEntity sourcePlayer, String teamName) {
        Team team = getTeam(teamName);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("Team not found"));
            return;
        }

        MinecraftServer server = sourcePlayer.getServer();
        if (server == null) return;

        sourcePlayer.sendMessage(Text.literal(teamName + " members:"));
        for (UUID uuid : team.members) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                sourcePlayer.sendMessage(Text.literal
                        ("- " + player.getName().getString() + " (" + getMemberRank(player.getUuid()) + ")"), false);
            } else {
                sourcePlayer.sendMessage(Text.literal("- Unknown Player (" + uuid + ")" + " (" + getMemberRank(uuid) + ")"), false);
            }
        }
    }

    public void listTeams(ServerPlayerEntity sourcePlayer) {
        if (teams.isEmpty()){
            sourcePlayer.sendMessage(Text.literal("Not teams found"));
            return;
        }

        sourcePlayer.sendMessage(Text.literal("Available Teams:"));
        for (String team : teams.keySet()) {
            sourcePlayer.sendMessage(Text.literal("- " + team), false);
        }
    }

    public void kickMember(ServerPlayerEntity player, String memberName){
        UUID playerId = player.getUuid();
        Team team = getPlayerTeam(playerId);
        if (team == null){
            player.sendMessage(Text.literal("You are not part of a team"));
            return;
        }
        if (!team.isLeader(playerId) && !team.isSubleader(playerId)) {
            player.sendMessage(Text.literal("You don't have the required permission to do that"));
            return;
        }

        ServerPlayerEntity member = Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayer(memberName);
        if (member == null) {
            player.sendMessage(Text.literal("Member not found"));
            return;
        }

        UUID memberUuid = member.getUuid();
        if (team.isLeader(memberUuid)) {
            player.sendMessage(Text.literal("You can't kick the leader"));
            return;
        }
        if (team.isSubleader(memberUuid) && !team.isLeader(playerId)) {
            player.sendMessage(Text.literal("Only the leader can kick subleaders"));
            return;
        }

        team.subleaders.remove(memberUuid);
        team.members.remove(memberUuid);
        sendGeneralMessage(player.getServer(), team, memberName + " has been kicked from the team");
    }

    public void promoteSubleader(ServerPlayerEntity player, String memberName){
        UUID playerId = player.getUuid();
        Team team = getPlayerTeam(playerId);
        if (team == null){
            player.sendMessage(Text.literal("You are not part of a team"));
            return;
        }
        if (!team.isLeader(playerId)) {
            player.sendMessage(Text.literal("Only the leader can promote members to be subleaders"));
            return;
        }

        ServerPlayerEntity member = Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayer(memberName);
        if (member == null) {
            player.sendMessage(Text.literal("Member not found"));
            return;
        }

        UUID memberUuid = member.getUuid();
        if (!team.members.contains(memberUuid)) {
            player.sendMessage(Text.literal(memberName + " is not a member of the team"));
            return;
        }

        if (team.isSubleader(memberUuid)){
            team.subleaders.remove(memberUuid);
            sendGeneralMessage(player.getServer(), team, memberName + " has been demoted from subleader");
            return;
        }
        team.subleaders.add(memberUuid);
        sendGeneralMessage(player.getServer(), team, memberName + " has been promoted to subleader");
    }

    public void listJoinRequests(ServerPlayerEntity sourcePlayer){
        UUID sourceUuid = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceUuid);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }

        MinecraftServer server = sourcePlayer.getServer();
        if (server == null) return;

        sourcePlayer.sendMessage(Text.literal("Team join requests:"));
        for (UUID uuid : team.joinRequests) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                sourcePlayer.sendMessage(Text.literal("- " + player.getName().getString()), false);
            } else {
                sourcePlayer.sendMessage(Text.literal("- Unknown Player (" + uuid + ")"), false);
            }
        }
    }

    public void acceptJoinRequest(ServerPlayerEntity sourcePlayer, String requestName){
        UUID sourceId = sourcePlayer.getUuid();
        Team team = getPlayerTeam(sourceId);
        if (team == null){
            sourcePlayer.sendMessage(Text.literal("You are not part of a team"));
            return;
        }
        if (!team.isLeader(sourceId) && !team.isSubleader(sourceId) && !team.anyMemberCanApprove){
            sourcePlayer.sendMessage(Text.literal("You don't have the required permission to do that"));
            return;
        }

        MinecraftServer server = sourcePlayer.getServer();
        if (server == null) return;

        ServerPlayerEntity request = server.getPlayerManager().getPlayer(requestName);
        if (request == null){
            sourcePlayer.sendMessage(Text.literal("Request not found"));
            return;
        }

        UUID requestUuid = request.getUuid();
        team.members.add(requestUuid);
        sendGeneralMessage(server, sourceId, team, requestName + " has joined the team");
        sourcePlayer.sendMessage(Text.literal("Accepted join request from" + requestName));
    }

    public CompletableFuture<Suggestions> suggestTeamNames(ServerPlayerEntity requester, SuggestionsBuilder builder) {
        for (String teamName : teams.keySet()) {
            builder.suggest(teamName);
        }
        return builder.buildFuture();
    }

    public CompletableFuture<Suggestions> suggestJoinRequestPlayers(ServerPlayerEntity requester, SuggestionsBuilder builder) {
        Team team = getPlayerTeam(requester.getUuid());
        if (team == null) return builder.buildFuture();

        MinecraftServer server = requester.getServer();
        if (server == null) return builder.buildFuture();

        for (UUID uuid : team.joinRequests) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                builder.suggest(player.getName().getString());
            }
        }
        return builder.buildFuture();
    }

    public CompletableFuture<Suggestions> suggestTeamMembers(ServerPlayerEntity requester, SuggestionsBuilder builder) {
        Team team = getPlayerTeam(requester.getUuid());
        if (team == null) return builder.buildFuture();

        MinecraftServer server = requester.getServer();
        if (server == null) return builder.buildFuture();

        for (UUID memberUuid : team.members) {
            ServerPlayerEntity member = server.getPlayerManager().getPlayer(memberUuid);
            if (member != null) {
                builder.suggest(member.getName().getString());
            }
        }
        return builder.buildFuture();
    }
}

