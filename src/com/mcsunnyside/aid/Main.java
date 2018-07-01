package com.mcsunnyside.aid;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {

	Map<Location, Block> SignLocCache = new HashMap<>();
	boolean Debug = false;
	public static Economy economy = null;

	// Vault api
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		Debug = getConfig().getBoolean("Settings.Debug");
		Bukkit.getPluginManager().registerEvents(this, this);
		getLogger().info("AdvancedIronDoor now enabled!");
		if (!setupEconomy()) {
			getLogger().warning("LoadFailed! AdvancedIronDoor can't found hard depend: Vault");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void InitSign(BlockPlaceEvent e) {
		if (e.getBlock().getType().equals(Material.WALL_SIGN)) {
			SignLocCache.put(e.getBlock().getLocation(), e.getBlockAgainst());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void PlaceSign(SignChangeEvent e) {
		boolean AlreadyHaveASpecialChoose = false;
		Location BlockAgainst = null;
		if (!e.getBlock().getType().equals(Material.WALL_SIGN)) {
			return;
		}
		try {
			BlockAgainst = SignLocCache.get(e.getBlock().getLocation()).getLocation();
		} catch (Exception error) {
			error.printStackTrace();
			e.getPlayer().sendMessage("¡ìcAn internal error happend,Please report to your server administrators.");
			getLogger().warning(
					"An internal error happed:We can't found player placed sign in cache. Please report this error,thanks!");
			return;
		}
		if (e.getBlock().getWorld()
				.getBlockAt((int) BlockAgainst.getX(), (int) BlockAgainst.getY() - 1, (int) BlockAgainst.getZ())
				.getType() != Material.IRON_DOOR_BLOCK
				&& e.getBlock().getWorld()
						.getBlockAt((int) BlockAgainst.getX() + 1, (int) BlockAgainst.getY() - 1,
								(int) BlockAgainst.getZ())
						.getType() != Material.IRON_DOOR_BLOCK
				&& e.getBlock().getWorld()
						.getBlockAt((int) BlockAgainst.getX() - 1, (int) BlockAgainst.getY() - 1,
								(int) BlockAgainst.getZ())
						.getType() != Material.IRON_DOOR_BLOCK
				&& e.getBlock().getWorld()
						.getBlockAt((int) BlockAgainst.getX(), (int) BlockAgainst.getY() - 1,
								(int) BlockAgainst.getZ() + 1)
						.getType() != Material.IRON_DOOR_BLOCK
				&& e.getBlock().getWorld().getBlockAt((int) BlockAgainst.getX(), (int) BlockAgainst.getY() - 1,
						(int) BlockAgainst.getZ() - 1).getType() != Material.IRON_DOOR_BLOCK) {
			if (Debug) {
				getLogger().info("No passed door check y1.");
			}
			return;
		}

		if (e.getBlock().getWorld()
				.getBlockAt((int) BlockAgainst.getX(), (int) BlockAgainst.getY() - 2, (int) BlockAgainst.getZ())
				.getType() != Material.IRON_DOOR_BLOCK
				&& e.getBlock().getWorld()
						.getBlockAt((int) BlockAgainst.getX() + 1, (int) BlockAgainst.getY() - 2,
								(int) BlockAgainst.getZ())
						.getType() != Material.IRON_DOOR_BLOCK
				&& e.getBlock().getWorld()
						.getBlockAt((int) BlockAgainst.getX() - 1, (int) BlockAgainst.getY() - 2,
								(int) BlockAgainst.getZ())
						.getType() != Material.IRON_DOOR_BLOCK
				&& e.getBlock().getWorld()
						.getBlockAt((int) BlockAgainst.getX(), (int) BlockAgainst.getY() - 2,
								(int) BlockAgainst.getZ() + 1)
						.getType() != Material.IRON_DOOR_BLOCK
				&& e.getBlock().getWorld().getBlockAt((int) BlockAgainst.getX(), (int) BlockAgainst.getY() - 2,
						(int) BlockAgainst.getZ() - 1).getType() != Material.IRON_DOOR_BLOCK) {
			if (Debug) {
				getLogger().info("No passed door check y2.");
			}
			return;
		}
		if (Debug) {
			getLogger().info("Check passed.");
		}
		Player Creater = e.getPlayer(); // Build AdvancedIronDoor
		String Line2 = e.getLine(1).toLowerCase();
		String Line3 = e.getLine(2).toLowerCase();
		if (Debug) {
			getLogger().info(e.getLine(0));
		}
		if (!e.getLine(0).equals(getConfig().getString("Strings.CheckLine"))) { // Check AdvancedIronDoor
			return;
		}
		if (Line2.isEmpty()) {
			e.getPlayer().sendMessage(getConfig().getString("Messages.CreateFailed"));
			return;
		}
		if (Debug) {
			getLogger().info("Checking permission:create");
		}
		if (!Creater.hasPermission("advancedirondoor.canbuild")) {
			Creater.sendMessage(getConfig().getString("Messages.NoPermissionToCreate"));
			return;
		}
		if (Debug) {
			getLogger().info("Checking permission:emptyinventory");
		}
		if (Line2.equals("null")) {
			if (!Creater.hasPermission("advancedirondoor.build.emptyinventory")) {
				Creater.sendMessage(getConfig().getString("Messages.NoPermissionEmptyInventory"));
				return;
			}
		}
		if (Line2.equals("mne")) {
			if (!Creater.hasPermission("advancedirondoor.build.mustnoeffect")) {
				Creater.sendMessage(getConfig().getString("Messages.NoPermissionMustNoEffect"));
				return;
			}
			if (Debug) {
				getLogger().info("Checking permission:needtopayvault");
			}
			if (Line2.equals("npv")) {
				if (AlreadyHaveASpecialChoose) {
					if (Line3.isEmpty()) {
						Creater.sendMessage(getConfig().getString("Messages.Line3IsNull"));
						return;
					}
					Creater.sendMessage(getConfig().getString("Messages.AlreadyHaveASpecialChoose"));
					return;
				}
				if (!Creater.hasPermission("advancedirondoor.build.needtopayvault")) {
					Creater.sendMessage(getConfig().getString("Messages.NoPermissionNeedToPayVault"));
					return;
				} else {
					if (!Line3.matches("[0-9]+")) {
						Creater.sendMessage(getConfig().getString("Messages.Line3FormatError"));
						return;
					}
					AlreadyHaveASpecialChoose = true;
				}
			}
			if (Debug) {
				getLogger().info("Checking permission:mustnopermission");
			}
			if (Line2.equals("mnp")) {
				if (AlreadyHaveASpecialChoose) {
					if (Line3.isEmpty()) {
						Creater.sendMessage(getConfig().getString("Messages.Line3IsNull"));
						return;
					}
					Creater.sendMessage(getConfig().getString("Messages.AlreadyHaveASpecialChoose"));
					return;
				}
				if (!Creater.hasPermission("advancedirondoor.build.mustnopermission")) {
					Creater.sendMessage(getConfig().getString("Messages.NoPermissionMustNoPermission"));
					return;
				} else {
					AlreadyHaveASpecialChoose = true;
				}
			}
			if (Debug) {
				getLogger().info("Checking permission:musthavepermission");
			}
			if (Line2.indexOf("mhp") != -1) {
				if (AlreadyHaveASpecialChoose) {
					if (Line3.isEmpty()) {
						Creater.sendMessage(getConfig().getString("Messages.Line3IsNull"));
						return;
					}
					Creater.sendMessage(getConfig().getString("Messages.AlreadyHaveASpecialChoose"));
					return;
				}
				if (!Creater.hasPermission("advancedirondoor.build.musthavepermission")) {
					Creater.sendMessage(getConfig().getString("Messages.NoPermissionMustHavePermission"));
					return;
				} else {
					AlreadyHaveASpecialChoose = true;
				}
			}
			if (AlreadyHaveASpecialChoose) {
				if (Line3.isEmpty()) {
					Creater.sendMessage(getConfig().getString("Messages.Line3IsNull"));
					return;
				}
			}
			if (Debug) {
				getLogger().info("Calling the CovertSign");
			}
		}
		// Check Done,Convert the sign!
		this.ConvertSign(e);
	}

	public void ConvertSign(SignChangeEvent e) {
		if (Debug) {
			getLogger().info("Cleaning the cache...");
		}
		SignLocCache.remove(e.getBlock().getLocation());
		if (Debug) {
			getLogger().info("Set line1 to firstline string.");
		}
		e.setLine(0, getConfig().getString("Strings.FirstLine"));
		if (Debug) {
			getLogger().info("Reading line2.");
		}
		String Line2 = e.getLine(1).toLowerCase();
		boolean SuccessfullyCreate = false;
		if (Line2.equals("null")) {
			e.setLine(1, getConfig().getString("Strings.EmptyInventory"));
			SuccessfullyCreate = true;
		}
		if (Line2.equals("npv")) {
			e.setLine(1, getConfig().getString("Strings.NeedToPayVault"));
			SuccessfullyCreate = true;
		}
		if (Line2.equals("mnp")) {
			e.setLine(1, getConfig().getString("Strings.MustNoPermission"));
			SuccessfullyCreate = true;
		}
		if (Line2.equals("mhp")) {
			e.setLine(1, getConfig().getString("Strings.MustHavePermission"));
			SuccessfullyCreate = true;
		}
		if (Line2.equals("mne")) {
			e.setLine(1, getConfig().getString("Strings.MustNoEffect"));
			SuccessfullyCreate = true;
		}
		if (!SuccessfullyCreate) {
			e.setCancelled(true);
		}
		e.setLine(3, e.getPlayer().getName());
		e.getPlayer().sendMessage(getConfig().getString("Messages.SuccessfullyCreate"));
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void PlayerUseIronDoor(PlayerInteractEvent e) {
		Player User = e.getPlayer();
		if (Debug) {
			getLogger().info("InteractEvent got it!");
		}

		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.hasBlock()) {
			if (Debug) {
				getLogger().info("InteractEvent ingored:Not a right_block action.");
			}
			return;
		}
		if (!e.getClickedBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
			return;
		}
		if (Debug) {
			getLogger().info("Starting irondoor check.");
		}
		boolean Enter = false; // In or out.
		Block ClickedBlock = e.getClickedBlock();
		if (ClickedBlock.getRelative(BlockFace.UP).getType().equals(Material.WALL_SIGN)) {
			ClickedBlock = ClickedBlock.getRelative(BlockFace.UP, 2);
		} else {
			ClickedBlock = ClickedBlock.getRelative(BlockFace.UP);
		}
		BlockFace Face = null;
		Sign LockSign = null;
		if (ClickedBlock.getRelative(e.getBlockFace()).getType().equals(Material.WALL_SIGN)) {
			LockSign = (Sign) ClickedBlock.getRelative(e.getBlockFace()).getState();
			if (LockSign.getLine(0).equals(getConfig().getString("Strings.FirstLine"))) {
				Face = e.getBlockFace();
			}
			if (Face == null && ClickedBlock.getRelative(e.getBlockFace().getOppositeFace()).getType()
					.equals(Material.WALL_SIGN)) {
				LockSign = (Sign) ClickedBlock.getRelative(e.getBlockFace().getOppositeFace()).getState();
				if (LockSign.getLine(0).equals(getConfig().getString("Strings.FirstLine"))) {
					Face = e.getBlockFace();
				}
			}
			if (Face == null && LockSign.getBlock().getRelative(e.getBlockFace().getOppositeFace()).getType()
					.equals(Material.WALL_SIGN)) {
				LockSign = (Sign) ClickedBlock.getRelative(e.getBlockFace().getOppositeFace()).getState();
				if (LockSign.getLine(0).equals(getConfig().getString("Strings.FirstLine"))) {
					Face = e.getBlockFace().getOppositeFace();
				}
			}
			if (Face == null) {
				// Not our need door.
				return;
			}

			if (LockSign.getLine(1).length() < 0) {
				User.sendMessage(getConfig().getString("Messages.WrongSign"));
				return;
			}
			if (ClickedBlock.getRelative(BlockFace.DOWN).getLocation().distance(e.getPlayer().getEyeLocation()) > 3) {
				User.sendMessage(getConfig().getString("Messages.TooFar"));
				e.setCancelled(true);
				return;
			}
			if (e.getBlockFace().equals(Face)) {
				Enter = true;
			} else {
				if (e.getBlockFace().getOppositeFace().equals(Face)) {
					Enter = false;
				} else {
					User.sendMessage(getConfig().getString("Messages.DoorSide"));
					e.setCancelled(true);
					return;
				}
			}
		}
		if (!User.isSneaking()) {
			return;
		}
		if (!User.hasPermission("advancedirondoor.use")) {
			User.sendMessage(getConfig().getString("Messages.NoPermissionUse"));
			e.setCancelled(true);
			return;
		}
		if (LockSign == null) {
			return;
		}
		String Line2 = LockSign.getLine(1);
		String Line3 = LockSign.getLine(2);

		boolean IsOwner = false;
		if (LockSign.getLine(3).equals(User.getName())) {
			IsOwner = true;
		}
		if (!IsOwner) {
			if (Line2.equals(getConfig().getString("Strings.EmptyInventory"))) {
				PlayerInventory Inv = e.getPlayer().getInventory();
				for (ItemStack item : Inv) {
					if (item != null && item.getType() != Material.AIR) {
						User.sendMessage(getConfig().getString("Messages.NoEmptyInventory"));
						return;
					}
				}
			}

			if (Line2.equals(getConfig().getString("Strings.MustNoPermission"))) {
				if (User.hasPermission(Line3)) {
					User.sendMessage(getConfig().getString("Messages.MustNoPermission"));
					return;
				}
			}

			if (Line2.equals(getConfig().getString("Strings.MustHavePermission"))) {
				if (!User.hasPermission(Line3)) {
					User.sendMessage(getConfig().getString("Messages.MustHavePermission"));
					return;
				}
			}
			if (Line2.equals(getConfig().getString("Strings.MustNoEffect"))) {
				if (!User.getActivePotionEffects().isEmpty()) {
					User.sendMessage(getConfig().getString("Messages.MustHavePermission"));
					return;
				}
			}
			if (Line2.equals(getConfig().getString("Strings.NeedToPayVault"))) { // This must check last.
				if (Integer.parseInt(Line3) > 0 && economy.has(User.getName(), Integer.parseInt(Line3))) {
					// Pay to creater
					Main.economy.withdrawPlayer(User.getName(), Integer.parseInt(Line3));
					Main.economy.depositPlayer(LockSign.getLine(3), Integer.parseInt(Line3));
					String PayMsg = getConfig().getString("Messages.PayToPlayer");
					PayMsg.replaceAll("%0", Line3);
					PayMsg.replaceAll("%1", LockSign.getLine(3));
					User.sendMessage(PayMsg);
				} else {
					User.sendMessage(getConfig().getString("Messages.NoEnoughVault"));
					return;
				}
			}
		} else {
			User.sendMessage(getConfig().getString("Messages.OwnerPassedDoor"));
		}

		Location Target = null;
		if (Enter) {
			Face = Face.getOppositeFace();
			Target = new Location(e.getClickedBlock().getWorld(),
					e.getClickedBlock().getRelative(Face).getLocation().getBlockX() + 0.5D,
					ClickedBlock.getRelative(BlockFace.DOWN, 2).getLocation().getY(),
					e.getClickedBlock().getRelative(Face).getLocation().getBlockZ() + 0.5D, User.getLocation().getYaw(),
					User.getLocation().getPitch());
		} else {
			Target = new Location(e.getClickedBlock().getWorld(),
					e.getClickedBlock().getRelative(Face).getLocation().getBlockX() + 0.5D,
					ClickedBlock.getRelative(BlockFace.DOWN, 2).getLocation().getY(),
					e.getClickedBlock().getRelative(Face).getLocation().getBlockZ() + 0.5D, User.getLocation().getYaw(),
					User.getLocation().getPitch());

		}
		User.teleport(Target);
		e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
		e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
		e.setCancelled(true);
	}

	// public boolean IsLockedDoor(Block block) {
	// if (block.getType() != Material.IRON_DOOR) {
	// return false;
	// }
	// Block check;
	// if (block.getRelative(BlockFace.UP).getType() == Material.IRON_DOOR) {
	// check = block.getRelative(BlockFace.UP, 2);
	// } else {
	// check = block.getRelative(BlockFace.UP);
	// }
	// BlockFace[] arrayOfBlockFace;
	// final BlockFace[] FOUR_FACE = { BlockFace.EAST, BlockFace.SOUTH,
	// BlockFace.WEST, BlockFace.NORTH };
	// int j = (arrayOfBlockFace = FOUR_FACE).length;
	// for (int i = 0; i < j; i++) {
	// BlockFace bf = arrayOfBlockFace[i];
	// if (check.getRelative(bf).getType() == Material.IRON_DOOR) {
	// org.bukkit.block.Sign sign = (org.bukkit.block.Sign)
	// check.getRelative(bf).getState();
	// if (sign.getLine(0).equals(getConfig().getString("Strings.FirstLine"))) {
	// return true;
	// }
	// }
	// }
	// return false;
	// }

	// @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	// public void onEntityInteract(EntityInteractEvent e) {
	// if (!(e.getEntity() instanceof Player) && IsLockedDoor(e.getBlock())) {
	// e.setCancelled(true);
	// }
	// }
	//
	// @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	// public void onEntityBreakDoor(EntityBreakDoorEvent e) {
	// if (!(e.getEntity() instanceof Player) && (IsLockedDoor(e.getBlock()))) {
	// e.setCancelled(true);
	// }
	// }

	// @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	// public void onBlockRedstone(BlockRedstoneEvent e) {
	// if (e.getBlock().getType() == Material.IRON_DOOR) {
	// Block b;
	// if (e.getBlock().getRelative(BlockFace.UP).getType() == Material.IRON_DOOR) {
	// b = e.getBlock().getRelative(BlockFace.UP, 2);
	// } else {
	// b = e.getBlock().getRelative(BlockFace.UP);
	// }
	// BlockFace[] arrayOfBlockFace;
	// final BlockFace[] FOUR_FACE = { BlockFace.EAST, BlockFace.SOUTH,
	// BlockFace.WEST, BlockFace.NORTH };
	// int j = (arrayOfBlockFace = FOUR_FACE).length;
	// for (int i = 0; i < j; i++) {
	// BlockFace bf = arrayOfBlockFace[i];
	// if (b.getRelative(bf).getType() == Material.IRON_DOOR) {
	// org.bukkit.block.Sign sign = (org.bukkit.block.Sign)
	// b.getRelative(bf).getState();
	// if (sign.getLine(0).equals(getConfig().getString("Strings.FirstLine"))) {
	// e.setNewCurrent(e.getOldCurrent());
	// return;
	// }
	// }
	// }
	// }
	// }
}
