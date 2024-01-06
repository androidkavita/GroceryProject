package com.easym.vegie.model;

import com.easym.vegie.Utils.EnumClicks;

import java.util.ArrayList;

public class MenuModel {
    private int menuIcon;
    private String menuName;
    private int arrowIcon;
    private ArrayList<MenuModel> menuList;
    EnumClicks clicks;

    private MenuModel menuModel;

    public MenuModel() {
    }

    public MenuModel(String menuName, EnumClicks clicks) {
        this.menuIcon = menuIcon;
        this.menuName = menuName;
        this.arrowIcon = arrowIcon;
        this.menuList = menuList;
        this.clicks = clicks;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public EnumClicks getClicks() {
        return clicks;
    }

    public void setClicks(EnumClicks clicks) {
        this.clicks = clicks;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getArrowIcon() {
        return arrowIcon;
    }

    public void setArrowIcon(int arrowIcon) {
        this.arrowIcon = arrowIcon;
    }

    public ArrayList<MenuModel> getMenuList() {
        return menuList;
    }

    public void setMenuList(ArrayList<MenuModel> menuList) {
        this.menuList = menuList;
    }

    public ArrayList<MenuModel> addMenuData() {
        menuList = new ArrayList<>();


        menuModel = new MenuModel("My Orders", EnumClicks.MY_ORDER);
        menuList.add(menuModel);


        menuModel = new MenuModel("Search Products", EnumClicks.SEARCH_PRODUCT);
        menuList.add(menuModel);

        menuModel = new MenuModel("My Cart", EnumClicks.MY_CART);
        menuList.add(menuModel);

        menuModel = new MenuModel("Offers", EnumClicks.OFFERS);
        menuList.add(menuModel);


        menuModel = new MenuModel("Saved Quotations", EnumClicks.SAVED_QUOTATIONS);
        menuList.add(menuModel);


        menuModel = new MenuModel("Refund Policy", EnumClicks.REFUND_POLICY);
        menuList.add(menuModel);

        menuModel = new MenuModel("Reach Us", EnumClicks.REACH_US);
        menuList.add(menuModel);

        menuModel = new MenuModel("Contact Us", EnumClicks.CONTACT_US);
        menuList.add(menuModel);

        menuModel = new MenuModel("Help", EnumClicks.HELP);
        menuList.add(menuModel);

        menuModel = new MenuModel("FAQ", EnumClicks.FAQ);
        menuList.add(menuModel);

        menuModel = new MenuModel("About Us", EnumClicks.ABOUT_US);
        menuList.add(menuModel);

        menuModel = new MenuModel("Log Out", EnumClicks.LOGOUT);
        menuList.add(menuModel);

        return menuList;
    }


}