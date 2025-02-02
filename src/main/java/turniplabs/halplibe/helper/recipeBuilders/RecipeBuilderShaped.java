package turniplabs.halplibe.helper.recipeBuilders;

import net.minecraft.core.block.Block;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.ItemStack;
import turniplabs.halplibe.helper.RecipeBuilder;

import java.util.Arrays;
import java.util.HashMap;

public class RecipeBuilderShaped extends RecipeBuilderBase{
    protected String[] shape; // Only used for shaped recipes
    protected int width;
    protected int height;
    protected boolean consumeContainer = false; // Only used for shapedRecipes
    protected final HashMap<Character, RecipeSymbol> symbolShapedMap = new HashMap<>();
    public RecipeBuilderShaped(String modID){
        super(modID);
    }
    public RecipeBuilderShaped(String modID, String... shape) {
        super(modID);
        setShapeLocal(shape);
    }
    @SuppressWarnings({"unused"})
    public RecipeBuilderShaped setShape(String... shapeTemplate){
        RecipeBuilderShaped builder = this.clone(this);
        builder.setShapeLocal(shapeTemplate);
        return builder;
    }
    protected void setShapeLocal(String... shape){
        if (shape == null){
            throw new IllegalArgumentException("Shape Template cannot be set to null!");
        }
        if (shape.length == 0){
            throw new IllegalArgumentException("Shape Template cannot have a size of 0!");
        }
        if (shape.length > 3){
            throw new IllegalArgumentException("Shape Template height cannot exceed 3!\n" + Arrays.toString(shape));
        }
        if (shape[0].length() > 3){
            throw new IllegalArgumentException("Shape Template width cannot exceed 3!\n" + Arrays.toString(shape));
        }
        this.height = shape.length;
        this.width = shape[0].length();

        // Gets the max width
        for (int y = 0; y < this.height; y++) {
            this.width = Math.max(this.width, shape[y].length());
        }

        // Ensures that the recipe shape is always square
        String[] internalShape = new String[height];
        for (int y = 0; y < internalShape.length; y++) {
            StringBuilder builder = new StringBuilder();
            String row = shape[y];
            for (int x = 0; x < width; x++) {
                if (x >= row.length()){
                    builder.append(" ");
                } else {
                    builder.append(row.charAt(x));
                }
            }
            internalShape[y] = builder.toString();
        }

        this.shape = internalShape;
    }
    @SuppressWarnings({"unused"})
    public RecipeBuilderShaped setConsumeContainer(boolean consumeContainer){
        RecipeBuilderShaped builder = this.clone(this);
        builder.consumeContainer = consumeContainer;
        return builder;
    }
    @SuppressWarnings({"unused"})
    public RecipeBuilderShaped addInput(char templateSymbol, IItemConvertible stack){
        return addInput(templateSymbol, stack, 0);
    }
    @SuppressWarnings({"unused"})
    public RecipeBuilderShaped addInput(char templateSymbol, IItemConvertible stack, int meta){
        ItemStack _stack = stack.getDefaultStack();
        _stack.setMetadata(meta);
        return addInput(templateSymbol, _stack);
    }
    @SuppressWarnings({"unused"})
    public RecipeBuilderShaped addInput(char templateSymbol, ItemStack stack){
        return addInput(templateSymbol, new RecipeSymbol(stack));
    }
    @SuppressWarnings({"unused"})
    public RecipeBuilderShaped addInput(char templateSymbol, String itemGroup) {
        return addInput(templateSymbol, new RecipeSymbol(itemGroup));
    }
    @SuppressWarnings({"unused"})
    public RecipeBuilderShaped addInput(char templateSymbol, RecipeSymbol symbol){
        if (templateSymbol == ' ') throw new IllegalArgumentException("Cannot assign item to protected symbol ' ' pick a different symbol for your recipe input");
        RecipeBuilderShaped builder = this.clone(this);
        symbolShapedMap.put(templateSymbol, symbol);
        return builder;
    }
    @SuppressWarnings({"unchecked", "unused"})
    public void create(String recipeID, ItemStack outputStack) {
        if (shape == null) throw new RuntimeException("Shaped recipe: " + recipeID + " attempted to build without a assigned shape!!");
        RecipeSymbol[] recipe = new RecipeSymbol[height * width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Character cha = null;
                if (shape[y].length() > x) {
                    cha = shape[y].charAt(x);
                }
                RecipeSymbol tempplate = symbolShapedMap.get(cha);
                if (tempplate == null){
                    recipe[x + y * width] = null;
                } else {
                    recipe[x + y * width] = new RecipeSymbol(cha == null ? ' ' : cha, tempplate.getStack(), tempplate.getItemGroup());
                }

            }
        }
        ((RecipeGroup<RecipeEntryCrafting<?, ?>>) RecipeBuilder.getRecipeGroup(modID, "workbench", new RecipeSymbol(Block.workbench.getDefaultStack())))
                .register(recipeID, new RecipeEntryCraftingShaped(width, height, recipe, outputStack, consumeContainer));
    }
}
