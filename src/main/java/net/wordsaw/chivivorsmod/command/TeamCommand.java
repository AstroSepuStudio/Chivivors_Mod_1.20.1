package net.wordsaw.chivivorsmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.wordsaw.chivivorsmod.team.TeamData;

import java.util.Objects;

public class TeamCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(CommandManager.literal("chivivors_teams")
                    .then(CommandManager.literal("list")
                            .executes(ctx -> {
                                TeamData.get(ctx.getSource().getWorld()).listTeams(Objects.requireNonNull(ctx.getSource().getPlayer()));
                                return 1;
                            }))
                    .then(CommandManager.literal("members")
                            .executes(ctx -> {
                                TeamData.get(ctx.getSource().getWorld()).listMembers(Objects.requireNonNull(ctx.getSource().getPlayer()));
                                return 1;
                            })
                            .then(CommandManager.argument("teamName", StringArgumentType.string())
                                    .suggests((ctx, builder) ->
                                            TeamData.get(ctx.getSource().getWorld())
                                                    .suggestTeamNames(Objects.requireNonNull(ctx.getSource().getPlayer()), builder)
                                    )
                                    .executes(ctx -> {
                                        String teamName = StringArgumentType.getString(ctx, "teamName");
                                        TeamData.get(ctx.getSource().getWorld()).listMembers(Objects.requireNonNull(ctx.getSource().getPlayer()), teamName);
                                        return 1;
                                    })))
                    .then(CommandManager.literal("create")
                            .then(CommandManager.argument("teamName", StringArgumentType.string())
                                    .executes(ctx -> {
                                        String teamName = StringArgumentType.getString(ctx, "teamName");
                                        TeamData.get(ctx.getSource().getWorld()).createTeam(teamName, Objects.requireNonNull(ctx.getSource().getPlayer()));
                                        return 1;
                                    })))
                    .then(CommandManager.literal("join")
                            .then(CommandManager.argument("teamName", StringArgumentType.string())
                                    .suggests((ctx, builder) ->
                                            TeamData.get(ctx.getSource().getWorld())
                                                    .suggestTeamNames(Objects.requireNonNull(ctx.getSource().getPlayer()), builder)
                                    )
                                    .executes(ctx -> {
                                        String teamName = StringArgumentType.getString(ctx, "teamName");
                                        TeamData.get(ctx.getSource().getWorld()).joinTeam(Objects.requireNonNull(ctx.getSource().getPlayer()), teamName);
                                        return 1;
                                    })))
                    .then(CommandManager.literal("leave")
                            .executes(ctx -> {
                                TeamData.get(ctx.getSource().getWorld()).leaveTeam(Objects.requireNonNull(ctx.getSource().getPlayer()));
                                return 1;
                            }))
                    .then(CommandManager.literal("config")
                            .then(CommandManager.literal("friendlyfire")
                                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean value = BoolArgumentType.getBool(ctx, "value");
                                                TeamData.get(ctx.getSource().getWorld()).setFriendlyFire(Objects.requireNonNull(ctx.getSource().getPlayer()), value);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("requiresapproval")
                                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean value = BoolArgumentType.getBool(ctx, "value");
                                                TeamData.get(ctx.getSource().getWorld()).setRequiresApproval(Objects.requireNonNull(ctx.getSource().getPlayer()), value);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("anymembercanapprove")
                                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean value = BoolArgumentType.getBool(ctx, "value");
                                                TeamData.get(ctx.getSource().getWorld()).setAnyMemberCanApprove(Objects.requireNonNull(ctx.getSource().getPlayer()), value);
                                                return 1;
                                            }))))
                    .then(CommandManager.literal("manage")
                            .then(CommandManager.literal("joinrequests")
                                    .executes(ctx -> {
                                        TeamData.get(ctx.getSource().getWorld()).listJoinRequests(Objects.requireNonNull(ctx.getSource().getPlayer()));
                                        return 1;
                                    }))
                            .then(CommandManager.literal("subleader")
                                    .then(CommandManager.argument("memberName", StringArgumentType.string())
                                            .suggests((ctx, builder) ->
                                                    TeamData.get(ctx.getSource().getWorld())
                                                            .suggestTeamMembers(Objects.requireNonNull(ctx.getSource().getPlayer()), builder)
                                            )
                                            .executes(ctx -> {
                                                String memberName = StringArgumentType.getString(ctx, "memberName");
                                                TeamData.get(ctx.getSource().getWorld()).promoteSubleader(Objects.requireNonNull(ctx.getSource().getPlayer()), memberName);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("kick")
                                    .then(CommandManager.argument("memberName", StringArgumentType.string())
                                            .suggests((ctx, builder) ->
                                                    TeamData.get(ctx.getSource().getWorld())
                                                            .suggestTeamMembers(Objects.requireNonNull(ctx.getSource().getPlayer()), builder)
                                            )
                                            .executes(ctx -> {
                                                String memberName = StringArgumentType.getString(ctx, "memberName");
                                                TeamData.get(ctx.getSource().getWorld()).kickMember(Objects.requireNonNull(ctx.getSource().getPlayer()), memberName);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("accept")
                                    .then(CommandManager.argument("playerName", StringArgumentType.string())
                                            .suggests((ctx, builder) -> TeamData.get(ctx.getSource().getWorld())
                                                    .suggestJoinRequestPlayers(Objects.requireNonNull(ctx.getSource().getPlayer()), builder))
                                            .executes(ctx -> {
                                                String playerName = StringArgumentType.getString(ctx, "playerName");
                                                TeamData.get(ctx.getSource().getWorld()).acceptJoinRequest(Objects.requireNonNull(ctx.getSource().getPlayer()), playerName);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("reject")
                                    .then(CommandManager.argument("playerName", StringArgumentType.string())
                                            .suggests((ctx, builder) -> TeamData.get(ctx.getSource().getWorld())
                                                    .suggestJoinRequestPlayers(Objects.requireNonNull(ctx.getSource().getPlayer()), builder))
                                            .executes(ctx -> {
                                                String playerName = StringArgumentType.getString(ctx, "playerName");
                                                TeamData.get(ctx.getSource().getWorld()).rejectJoinRequest(Objects.requireNonNull(ctx.getSource().getPlayer()), playerName);
                                                return 1;
                                            }))
                                    .then(CommandManager.literal("all")
                                            .executes(ctx -> {
                                                TeamData.get(ctx.getSource().getWorld()).rejectAllJoinRequests(Objects.requireNonNull(ctx.getSource().getPlayer()));
                                                return 1;
                                            }))))
            );}
}
