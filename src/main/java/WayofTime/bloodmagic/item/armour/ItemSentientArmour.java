package WayofTime.bloodmagic.item.armour;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.registry.ModItems;

public class ItemSentientArmour extends ItemArmor implements ISpecialArmor
{
    public static String[] names = { "helmet", "chest", "legs", "boots" };

    public ItemSentientArmour(int armorType)
    {
        super(ItemArmor.ArmorMaterial.IRON, 0, armorType);
        setUnlocalizedName(Constants.Mod.MODID + ".sentientArmour.");
        setMaxDamage(250);
        setCreativeTab(BloodMagic.tabBloodMagic);
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack stack, DamageSource source, double damage, int slot)
    {
        double armourReduction = 0.0;
        double damageAmount = 0.25;

        if (this == ModItems.sentientArmourBoots || this == ModItems.sentientArmourHelmet)
        {
            damageAmount = 3d / 20d * 0.6;
        } else if (this == ModItems.sentientArmourLegs)
        {
            damageAmount = 6d / 20d * 0.6;
        } else if (this == ModItems.sentientArmourChest)
        {
            damageAmount = 0.64;
        }

        double armourPenetrationReduction = 0;

        int maxAbsorption = 100000;

        if (source.equals(DamageSource.drown))
        {
            return new ArmorProperties(-1, 0, 0);
        }

        if (source.equals(DamageSource.outOfWorld))
        {
            return new ArmorProperties(-1, 0, 0);
        }

        if (this == ModItems.sentientArmourChest)
        {
            armourReduction = 0.24 / 0.64; // This values puts it at iron level

            ItemStack helmet = player.getEquipmentInSlot(4);
            ItemStack leggings = player.getEquipmentInSlot(2);
            ItemStack boots = player.getEquipmentInSlot(1);

            if (helmet == null || leggings == null || boots == null)
            {
                damageAmount *= (armourReduction);

                return new ArmorProperties(-1, damageAmount, maxAbsorption);
            }

            if (helmet.getItem() instanceof ItemSentientArmour && leggings.getItem() instanceof ItemSentientArmour && boots.getItem() instanceof ItemSentientArmour)
            {
                double remainder = 1; // Multiply this number by the armour upgrades for protection

                armourReduction = armourReduction + (1 - remainder) * (1 - armourReduction);
                damageAmount *= (armourReduction);

                if (source.isUnblockable())
                {
                    return new ArmorProperties(-1, damageAmount * armourPenetrationReduction, maxAbsorption);
                }

                return new ArmorProperties(-1, damageAmount, maxAbsorption);
            }
        } else
        {
            if (source.isUnblockable())
            {
                return new ArmorProperties(-1, damageAmount * armourPenetrationReduction, maxAbsorption);
            }

            return new ArmorProperties(-1, damageAmount, maxAbsorption);
        }

        return new ArmorProperties(-1, 0, 0);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot)
    {
        if (armor.getItem() == ModItems.sentientArmourHelmet)
        {
            return 3;
        }

        if (armor.getItem() == ModItems.sentientArmourChest)
        {
            return 8;
        }

        if (armor.getItem() == ModItems.sentientArmourLegs)
        {
            return 6;
        }

        if (armor.getItem() == ModItems.sentientArmourBoots)
        {
            return 3;
        }

        return 5;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        return; // Armour shouldn't get damaged... for now
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {

        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        if (this == ModItems.sentientArmourChest || this == ModItems.sentientArmourHelmet || this == ModItems.sentientArmourBoots)
        {
            return "bloodmagic:models/armor/sentientArmour_layer_1.png";
        }

        if (this == ModItems.sentientArmourLegs)
        {
            return "bloodmagic:models/armor/sentientArmour_layer_2.png";
        } else
        {
            return null;
        }
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        super.onArmorTick(world, player, stack);

        if (world.isRemote)
        {
            return;
        }

        //TODO: Consume will - if the will drops to 0, return the contained item
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + names[armorType];
    }

    public void revertArmour(EntityPlayer player, ItemStack itemStack)
    {
        ItemStack stack = this.getContainedArmourStack(itemStack);
        player.inventory.armorInventory[3 - this.armorType] = stack;
    }

    public static void revertAllArmour(EntityPlayer player)
    {
        ItemStack[] armourInventory = player.inventory.armorInventory;
        for (ItemStack stack : armourInventory)
        {
            if (stack != null && stack.getItem() instanceof ItemSentientArmour)
            {
                ((ItemSentientArmour) stack.getItem()).revertArmour(player, stack);
            }
        }
    }

    public void setContainedArmourStack(ItemStack newArmour, ItemStack previousArmour)
    {
        if (newArmour == null || previousArmour == null)
        {
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        previousArmour.writeToNBT(tag);

        NBTTagCompound omegaTag = newArmour.getTagCompound();
        if (omegaTag == null)
        {
            omegaTag = new NBTTagCompound();
            newArmour.setTagCompound(omegaTag);
        }

        omegaTag.setTag("armour", tag);
    }

    public ItemStack getContainedArmourStack(ItemStack newArmour)
    {
        NBTTagCompound omegaTag = newArmour.getTagCompound();
        if (omegaTag == null)
        {
            return null;
        }

        NBTTagCompound tag = omegaTag.getCompoundTag("armour");
        ItemStack armourStack = ItemStack.loadItemStackFromNBT(tag);

        return armourStack;
    }

    public static boolean convertPlayerArmour(EntityPlayer player)
    {
        ItemStack[] armours = player.inventory.armorInventory;

        ItemStack helmetStack = armours[3];
        ItemStack chestStack = armours[2];
        ItemStack leggingsStack = armours[1];
        ItemStack bootsStack = armours[0];

        {
            ItemStack omegaHelmetStack = ((ItemSentientArmour) ModItems.sentientArmourHelmet).getSubstituteStack(helmetStack);
            ItemStack omegaChestStack = ((ItemSentientArmour) ModItems.sentientArmourChest).getSubstituteStack(chestStack);
            ItemStack omegaLeggingsStack = ((ItemSentientArmour) ModItems.sentientArmourLegs).getSubstituteStack(leggingsStack);
            ItemStack omegaBootsStack = ((ItemSentientArmour) ModItems.sentientArmourBoots).getSubstituteStack(bootsStack);

            armours[3] = omegaHelmetStack;
            armours[2] = omegaChestStack;
            armours[1] = omegaLeggingsStack;
            armours[0] = omegaBootsStack;

            return true;
        }
    }

    public ItemStack getSubstituteStack(ItemStack previousArmour)
    {
        ItemStack newArmour = new ItemStack(this);

        this.setContainedArmourStack(newArmour, previousArmour);

        return newArmour;
    }
}