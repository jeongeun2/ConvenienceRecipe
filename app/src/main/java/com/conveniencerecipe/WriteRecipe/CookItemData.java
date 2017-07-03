package com.conveniencerecipe.WriteRecipe;

/**
 * Created by ccei on 2016-08-04.
 */
public class CookItemData {
    public Integer cookImage, dropdownCookImage;
    public String cookText;

    public CookItemData(Integer cookImage, Integer dropdownCookImage, String cookText) {
        this.cookImage = cookImage;
        this.dropdownCookImage = dropdownCookImage;
        this.cookText = cookText;
    }

    public Integer getCookImage() {
        return cookImage;
    }

    public Integer getDropdownCookImage() { return  dropdownCookImage; }

    public String getCookText() {
        return cookText;
    }
}