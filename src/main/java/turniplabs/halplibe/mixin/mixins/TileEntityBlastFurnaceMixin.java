package turniplabs.halplibe.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.block.entity.TileEntityBlastFurnace;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeRegistry;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = TileEntityBlastFurnace.class, remap = false)
public class TileEntityBlastFurnaceMixin extends TileEntityFurnace {
    @Redirect(method = "canSmelt()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/data/registry/recipe/RecipeRegistry;getGroupFromKey(Ljava/lang/String;)Lnet/minecraft/core/data/registry/recipe/RecipeGroup;"))
    private RecipeGroup<RecipeEntryBlastFurnace> canSmeltFake(RecipeRegistry instance, String e){
        RecipeGroup<RecipeEntryBlastFurnace> group = groupEmulator();
        if (group != null) return groupEmulator();
        return instance.getGroupFromKey(e);
    }
    @Redirect(method = "smeltItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/data/registry/recipe/RecipeRegistry;getGroupFromKey(Ljava/lang/String;)Lnet/minecraft/core/data/registry/recipe/RecipeGroup;"))
    private RecipeGroup<RecipeEntryBlastFurnace> doSmeltFake(RecipeRegistry instance, String e){
        RecipeGroup<RecipeEntryBlastFurnace> group = groupEmulator();
        if (group != null) return groupEmulator();
        return instance.getGroupFromKey(e);
    }

    @Unique
    private RecipeGroup<RecipeEntryBlastFurnace> groupEmulator(){
        RecipeGroup<RecipeEntryBlastFurnace> group = new RecipeGroup<>(null);
        RecipeEntryBlastFurnace entryFurnace = getMatchingRecipe();
        if (entryFurnace == null) return null;
        group.register("don'tCareLol", getMatchingRecipe());
        return group;
    }
    @Unique
    private RecipeEntryBlastFurnace getMatchingRecipe(){
        List<RecipeEntryBlastFurnace> recipes = Registries.RECIPES.getAllBlastFurnaceRecipes();
        for (RecipeEntryBlastFurnace recipeEntryFurnace : recipes){
            if (recipeEntryFurnace.matches(furnaceItemStacks[0])){
                return recipeEntryFurnace;
            }
        }
        return null;
    }

    //TODO: REMOVE THIS WHEN 7.1 RELEASES FULLY!!!
    @Inject(method = "smeltItem", at = @At(value = "FIELD", target = "Lnet/minecraft/core/block/entity/TileEntityBlastFurnace;furnaceItemStacks:[Lnet/minecraft/core/item/ItemStack;", ordinal = 5))
    public void fixBug(CallbackInfo ci, @Local ItemStack stack){
        furnaceItemStacks[2].stackSize += stack.stackSize-1;
    }

}
