package com.easym.vegie.di


import com.easym.vegie.activities.*
import com.easym.vegie.fragment.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Binds all sub-components within the app.
 */

@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun SplashActivity(): SplashScreen

    @ContributesAndroidInjector
    abstract fun LoginActivity(): LoginPageActivity

    @ContributesAndroidInjector
    abstract fun RegisterActivity(): RegistrationActivity

    @ContributesAndroidInjector
    abstract fun ForgotPasswordActivity(): ForgotPasswordActivity

    @ContributesAndroidInjector
    abstract fun VerifyAccountActivity(): VerifyAccountActivity

    @ContributesAndroidInjector
    abstract fun EditProfileActivity(): EditProfileActivity

    @ContributesAndroidInjector
    abstract fun HomePageActivity(): HomePageActivity

    @ContributesAndroidInjector
    abstract fun LanguageActivity(): LanguageActivity

    @ContributesAndroidInjector
    abstract fun MyOrderActivity(): MyOrderDetailsActivity

    @ContributesAndroidInjector
    abstract fun MyProfilePageActivity(): MyProfilePageActivity

    @ContributesAndroidInjector
    abstract fun SelectLanguageActivity(): SelectLanguageActivity

    @ContributesAndroidInjector
    abstract fun CheckOutScreenActivity(): CheckOutScreenActivity

    @ContributesAndroidInjector
    abstract fun CameraXActivity(): CameraXActivity

    @ContributesAndroidInjector
    abstract fun AboutUsFragment(): AboutUsFragment

    @ContributesAndroidInjector
    abstract fun FAQFragment(): FAQFragment

    @ContributesAndroidInjector
    abstract fun HelpFragment(): HelpFragment

    @ContributesAndroidInjector
    abstract fun RefundPolicyFragment(): RefundPolicyFragment

    @ContributesAndroidInjector
    abstract fun ContactUsFragment(): ContactUsFragment

    @ContributesAndroidInjector
    abstract fun ReachUsFragment(): ReachUsFragment

    @ContributesAndroidInjector
    abstract fun SavedProductFragment(): SavedProductFragment

    @ContributesAndroidInjector
    abstract fun HomePageFragment(): HomePageFragment

    @ContributesAndroidInjector
    abstract fun MyOrderFragment(): MyOrderFragment

    @ContributesAndroidInjector
    abstract fun AddWishListActivity(): AddWishListActivity

    @ContributesAndroidInjector
    abstract fun OffersFragment(): OffersFragment

    @ContributesAndroidInjector
    abstract fun SearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun ShopByCategoryActivity(): ShopByCategoryActivity

    @ContributesAndroidInjector
    abstract fun SubCategoryShoppingFragment(): SubCategoryShoppingFragment

    @ContributesAndroidInjector
    abstract fun CreateNewPasswordActivity(): CreateNewPasswordActivity

    @ContributesAndroidInjector
    abstract fun ShopByQuotationActivity(): ShopByQuotationActivity

    //MyCartFragment
    @ContributesAndroidInjector
    abstract fun MyCartFragment(): MyCartListFragment

    @ContributesAndroidInjector
    abstract fun MyCartActivity(): MyCartActivity

    @ContributesAndroidInjector
    abstract fun ChangeLanguageFragment(): ChangeLanguageFragment

    @ContributesAndroidInjector
    abstract fun SavedQuotationsFragment(): SavedQuotationsFragment

    @ContributesAndroidInjector
    abstract fun AddAddressFragment() : AddAddressFragment

    @ContributesAndroidInjector
    abstract fun SavedQuotationsDetailsFragment() : SavedQuotationsDetailsFragment

    @ContributesAndroidInjector
    abstract fun MainCategoryShoppingFragment() : MainCategoryShoppingFragment

    @ContributesAndroidInjector
    abstract fun UserAddressListActivity() : UserAddressListActivity

    @ContributesAndroidInjector
    abstract fun FragmentContainerActivity() : FragmentContainerActivity
}