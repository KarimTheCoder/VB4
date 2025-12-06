package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final AkexorcistLibraryAccessors laccForAkexorcistLibraryAccessors = new AkexorcistLibraryAccessors(owner);
    private final AndroidxLibraryAccessors laccForAndroidxLibraryAccessors = new AndroidxLibraryAccessors(owner);
    private final CkettiLibraryAccessors laccForCkettiLibraryAccessors = new CkettiLibraryAccessors(owner);
    private final FirebaseLibraryAccessors laccForFirebaseLibraryAccessors = new FirebaseLibraryAccessors(owner);
    private final GithubLibraryAccessors laccForGithubLibraryAccessors = new GithubLibraryAccessors(owner);
    private final GoogleLibraryAccessors laccForGoogleLibraryAccessors = new GoogleLibraryAccessors(owner);
    private final GordonwongLibraryAccessors laccForGordonwongLibraryAccessors = new GordonwongLibraryAccessors(owner);
    private final KoinLibraryAccessors laccForKoinLibraryAccessors = new KoinLibraryAccessors(owner);
    private final KotlinLibraryAccessors laccForKotlinLibraryAccessors = new KotlinLibraryAccessors(owner);
    private final KotlinxLibraryAccessors laccForKotlinxLibraryAccessors = new KotlinxLibraryAccessors(owner);
    private final KyleduoLibraryAccessors laccForKyleduoLibraryAccessors = new KyleduoLibraryAccessors(owner);
    private final RoomLibraryAccessors laccForRoomLibraryAccessors = new RoomLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Dependency provider for <b>billing</b> with <b>com.android.billingclient:billing</b> coordinates and
     * with version reference <b>billing</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getBilling() {
        return create("billing");
    }

    /**
     * Dependency provider for <b>googleid</b> with <b>com.google.android.libraries.identity.googleid:googleid</b> coordinates and
     * with version reference <b>googleid</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getGoogleid() {
        return create("googleid");
    }

    /**
     * Dependency provider for <b>junit</b> with <b>junit:junit</b> coordinates and
     * with version reference <b>junit</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJunit() {
        return create("junit");
    }

    /**
     * Group of libraries at <b>akexorcist</b>
     */
    public AkexorcistLibraryAccessors getAkexorcist() {
        return laccForAkexorcistLibraryAccessors;
    }

    /**
     * Group of libraries at <b>androidx</b>
     */
    public AndroidxLibraryAccessors getAndroidx() {
        return laccForAndroidxLibraryAccessors;
    }

    /**
     * Group of libraries at <b>cketti</b>
     */
    public CkettiLibraryAccessors getCketti() {
        return laccForCkettiLibraryAccessors;
    }

    /**
     * Group of libraries at <b>firebase</b>
     */
    public FirebaseLibraryAccessors getFirebase() {
        return laccForFirebaseLibraryAccessors;
    }

    /**
     * Group of libraries at <b>github</b>
     */
    public GithubLibraryAccessors getGithub() {
        return laccForGithubLibraryAccessors;
    }

    /**
     * Group of libraries at <b>google</b>
     */
    public GoogleLibraryAccessors getGoogle() {
        return laccForGoogleLibraryAccessors;
    }

    /**
     * Group of libraries at <b>gordonwong</b>
     */
    public GordonwongLibraryAccessors getGordonwong() {
        return laccForGordonwongLibraryAccessors;
    }

    /**
     * Group of libraries at <b>koin</b>
     */
    public KoinLibraryAccessors getKoin() {
        return laccForKoinLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kotlin</b>
     */
    public KotlinLibraryAccessors getKotlin() {
        return laccForKotlinLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kotlinx</b>
     */
    public KotlinxLibraryAccessors getKotlinx() {
        return laccForKotlinxLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kyleduo</b>
     */
    public KyleduoLibraryAccessors getKyleduo() {
        return laccForKyleduoLibraryAccessors;
    }

    /**
     * Group of libraries at <b>room</b>
     */
    public RoomLibraryAccessors getRoom() {
        return laccForRoomLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class AkexorcistLibraryAccessors extends SubDependencyFactory {

        public AkexorcistLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>roundcornerprogressbar</b> with <b>com.akexorcist:RoundCornerProgressBar</b> coordinates and
         * with version reference <b>akexorcist.roundcornerprogressbar</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRoundcornerprogressbar() {
            return create("akexorcist.roundcornerprogressbar");
        }

    }

    public static class AndroidxLibraryAccessors extends SubDependencyFactory {
        private final AndroidxCredentialsLibraryAccessors laccForAndroidxCredentialsLibraryAccessors = new AndroidxCredentialsLibraryAccessors(owner);
        private final AndroidxDatastoreLibraryAccessors laccForAndroidxDatastoreLibraryAccessors = new AndroidxDatastoreLibraryAccessors(owner);
        private final AndroidxEspressoLibraryAccessors laccForAndroidxEspressoLibraryAccessors = new AndroidxEspressoLibraryAccessors(owner);
        private final AndroidxTestLibraryAccessors laccForAndroidxTestLibraryAccessors = new AndroidxTestLibraryAccessors(owner);

        public AndroidxLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>appcompat</b> with <b>androidx.appcompat:appcompat</b> coordinates and
         * with version reference <b>appcompat</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAppcompat() {
            return create("androidx.appcompat");
        }

        /**
         * Dependency provider for <b>cardview</b> with <b>androidx.cardview:cardview</b> coordinates and
         * with version reference <b>cardview</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCardview() {
            return create("androidx.cardview");
        }

        /**
         * Dependency provider for <b>constraintlayout</b> with <b>androidx.constraintlayout:constraintlayout</b> coordinates and
         * with version reference <b>constraintlayout</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getConstraintlayout() {
            return create("androidx.constraintlayout");
        }

        /**
         * Dependency provider for <b>material</b> with <b>com.google.android.material:material</b> coordinates and
         * with version reference <b>material</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMaterial() {
            return create("androidx.material");
        }

        /**
         * Dependency provider for <b>recyclerview</b> with <b>androidx.recyclerview:recyclerview</b> coordinates and
         * with version reference <b>recyclerview</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRecyclerview() {
            return create("androidx.recyclerview");
        }

        /**
         * Dependency provider for <b>tracing</b> with <b>androidx.tracing:tracing</b> coordinates and
         * with version reference <b>tracing</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTracing() {
            return create("androidx.tracing");
        }

        /**
         * Group of libraries at <b>androidx.credentials</b>
         */
        public AndroidxCredentialsLibraryAccessors getCredentials() {
            return laccForAndroidxCredentialsLibraryAccessors;
        }

        /**
         * Group of libraries at <b>androidx.datastore</b>
         */
        public AndroidxDatastoreLibraryAccessors getDatastore() {
            return laccForAndroidxDatastoreLibraryAccessors;
        }

        /**
         * Group of libraries at <b>androidx.espresso</b>
         */
        public AndroidxEspressoLibraryAccessors getEspresso() {
            return laccForAndroidxEspressoLibraryAccessors;
        }

        /**
         * Group of libraries at <b>androidx.test</b>
         */
        public AndroidxTestLibraryAccessors getTest() {
            return laccForAndroidxTestLibraryAccessors;
        }

    }

    public static class AndroidxCredentialsLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final AndroidxCredentialsPlayLibraryAccessors laccForAndroidxCredentialsPlayLibraryAccessors = new AndroidxCredentialsPlayLibraryAccessors(owner);

        public AndroidxCredentialsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>credentials</b> with <b>androidx.credentials:credentials</b> coordinates and
         * with version reference <b>credentials</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("androidx.credentials");
        }

        /**
         * Group of libraries at <b>androidx.credentials.play</b>
         */
        public AndroidxCredentialsPlayLibraryAccessors getPlay() {
            return laccForAndroidxCredentialsPlayLibraryAccessors;
        }

    }

    public static class AndroidxCredentialsPlayLibraryAccessors extends SubDependencyFactory {
        private final AndroidxCredentialsPlayServicesLibraryAccessors laccForAndroidxCredentialsPlayServicesLibraryAccessors = new AndroidxCredentialsPlayServicesLibraryAccessors(owner);

        public AndroidxCredentialsPlayLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>androidx.credentials.play.services</b>
         */
        public AndroidxCredentialsPlayServicesLibraryAccessors getServices() {
            return laccForAndroidxCredentialsPlayServicesLibraryAccessors;
        }

    }

    public static class AndroidxCredentialsPlayServicesLibraryAccessors extends SubDependencyFactory {

        public AndroidxCredentialsPlayServicesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>auth</b> with <b>androidx.credentials:credentials-play-services-auth</b> coordinates and
         * with version reference <b>credentials</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAuth() {
            return create("androidx.credentials.play.services.auth");
        }

    }

    public static class AndroidxDatastoreLibraryAccessors extends SubDependencyFactory {

        public AndroidxDatastoreLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>preferences</b> with <b>androidx.datastore:datastore-preferences</b> coordinates and
         * with version reference <b>androidx.datastore.preferences</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPreferences() {
            return create("androidx.datastore.preferences");
        }

    }

    public static class AndroidxEspressoLibraryAccessors extends SubDependencyFactory {

        public AndroidxEspressoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>androidx.test.espresso:espresso-core</b> coordinates and
         * with version reference <b>androidx.espresso.core</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("androidx.espresso.core");
        }

    }

    public static class AndroidxTestLibraryAccessors extends SubDependencyFactory {
        private final AndroidxTestExtLibraryAccessors laccForAndroidxTestExtLibraryAccessors = new AndroidxTestExtLibraryAccessors(owner);

        public AndroidxTestLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>androidx.test.ext</b>
         */
        public AndroidxTestExtLibraryAccessors getExt() {
            return laccForAndroidxTestExtLibraryAccessors;
        }

    }

    public static class AndroidxTestExtLibraryAccessors extends SubDependencyFactory {

        public AndroidxTestExtLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>junit</b> with <b>androidx.test.ext:junit</b> coordinates and
         * with version reference <b>androidx.test.ext.junit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunit() {
            return create("androidx.test.ext.junit");
        }

    }

    public static class CkettiLibraryAccessors extends SubDependencyFactory {
        private final CkettiEmailLibraryAccessors laccForCkettiEmailLibraryAccessors = new CkettiEmailLibraryAccessors(owner);

        public CkettiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>cketti.email</b>
         */
        public CkettiEmailLibraryAccessors getEmail() {
            return laccForCkettiEmailLibraryAccessors;
        }

    }

    public static class CkettiEmailLibraryAccessors extends SubDependencyFactory {
        private final CkettiEmailIntentLibraryAccessors laccForCkettiEmailIntentLibraryAccessors = new CkettiEmailIntentLibraryAccessors(owner);

        public CkettiEmailLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>cketti.email.intent</b>
         */
        public CkettiEmailIntentLibraryAccessors getIntent() {
            return laccForCkettiEmailIntentLibraryAccessors;
        }

    }

    public static class CkettiEmailIntentLibraryAccessors extends SubDependencyFactory {

        public CkettiEmailIntentLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>builder</b> with <b>de.cketti.mailto:email-intent-builder</b> coordinates and
         * with version reference <b>cketti.email.intent.builder</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBuilder() {
            return create("cketti.email.intent.builder");
        }

    }

    public static class FirebaseLibraryAccessors extends SubDependencyFactory {
        private final FirebaseUiLibraryAccessors laccForFirebaseUiLibraryAccessors = new FirebaseUiLibraryAccessors(owner);

        public FirebaseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>analytics</b> with <b>com.google.firebase:firebase-analytics</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAnalytics() {
            return create("firebase.analytics");
        }

        /**
         * Dependency provider for <b>auth</b> with <b>com.google.firebase:firebase-auth</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAuth() {
            return create("firebase.auth");
        }

        /**
         * Dependency provider for <b>bom</b> with <b>com.google.firebase:firebase-bom</b> coordinates and
         * with version reference <b>firebase.bom</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBom() {
            return create("firebase.bom");
        }

        /**
         * Dependency provider for <b>crashlytics</b> with <b>com.google.firebase:firebase-crashlytics</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCrashlytics() {
            return create("firebase.crashlytics");
        }

        /**
         * Dependency provider for <b>database</b> with <b>com.google.firebase:firebase-database</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getDatabase() {
            return create("firebase.database");
        }

        /**
         * Dependency provider for <b>storage</b> with <b>com.google.firebase:firebase-storage</b> coordinates and
         * with <b>no version specified</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getStorage() {
            return create("firebase.storage");
        }

        /**
         * Group of libraries at <b>firebase.ui</b>
         */
        public FirebaseUiLibraryAccessors getUi() {
            return laccForFirebaseUiLibraryAccessors;
        }

    }

    public static class FirebaseUiLibraryAccessors extends SubDependencyFactory {

        public FirebaseUiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>auth</b> with <b>com.firebaseui:firebase-ui-auth</b> coordinates and
         * with version reference <b>firebase.ui.auth</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAuth() {
            return create("firebase.ui.auth");
        }

    }

    public static class GithubLibraryAccessors extends SubDependencyFactory {
        private final GithubArimortyLibraryAccessors laccForGithubArimortyLibraryAccessors = new GithubArimortyLibraryAccessors(owner);
        private final GithubYbqLibraryAccessors laccForGithubYbqLibraryAccessors = new GithubYbqLibraryAccessors(owner);
        private final GithubZurcheLibraryAccessors laccForGithubZurcheLibraryAccessors = new GithubZurcheLibraryAccessors(owner);

        public GithubLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>github.arimorty</b>
         */
        public GithubArimortyLibraryAccessors getArimorty() {
            return laccForGithubArimortyLibraryAccessors;
        }

        /**
         * Group of libraries at <b>github.ybq</b>
         */
        public GithubYbqLibraryAccessors getYbq() {
            return laccForGithubYbqLibraryAccessors;
        }

        /**
         * Group of libraries at <b>github.zurche</b>
         */
        public GithubZurcheLibraryAccessors getZurche() {
            return laccForGithubZurcheLibraryAccessors;
        }

    }

    public static class GithubArimortyLibraryAccessors extends SubDependencyFactory {

        public GithubArimortyLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>floatingsearchview</b> with <b>com.github.arimorty:floatingsearchview</b> coordinates and
         * with version reference <b>github.arimorty.floatingsearchview</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getFloatingsearchview() {
            return create("github.arimorty.floatingsearchview");
        }

    }

    public static class GithubYbqLibraryAccessors extends SubDependencyFactory {
        private final GithubYbqAndroidLibraryAccessors laccForGithubYbqAndroidLibraryAccessors = new GithubYbqAndroidLibraryAccessors(owner);

        public GithubYbqLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>github.ybq.android</b>
         */
        public GithubYbqAndroidLibraryAccessors getAndroid() {
            return laccForGithubYbqAndroidLibraryAccessors;
        }

    }

    public static class GithubYbqAndroidLibraryAccessors extends SubDependencyFactory {

        public GithubYbqAndroidLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>spinkit</b> with <b>com.github.ybq:Android-SpinKit</b> coordinates and
         * with version reference <b>github.ybq.android.spinkit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSpinkit() {
            return create("github.ybq.android.spinkit");
        }

    }

    public static class GithubZurcheLibraryAccessors extends SubDependencyFactory {
        private final GithubZurchePlainLibraryAccessors laccForGithubZurchePlainLibraryAccessors = new GithubZurchePlainLibraryAccessors(owner);

        public GithubZurcheLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>github.zurche.plain</b>
         */
        public GithubZurchePlainLibraryAccessors getPlain() {
            return laccForGithubZurchePlainLibraryAccessors;
        }

    }

    public static class GithubZurchePlainLibraryAccessors extends SubDependencyFactory {

        public GithubZurchePlainLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>pie</b> with <b>com.github.zurche:plain-pie</b> coordinates and
         * with version reference <b>github.zurche.plain.pie</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPie() {
            return create("github.zurche.plain.pie");
        }

    }

    public static class GoogleLibraryAccessors extends SubDependencyFactory {
        private final GooglePlayLibraryAccessors laccForGooglePlayLibraryAccessors = new GooglePlayLibraryAccessors(owner);

        public GoogleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>google.play</b>
         */
        public GooglePlayLibraryAccessors getPlay() {
            return laccForGooglePlayLibraryAccessors;
        }

    }

    public static class GooglePlayLibraryAccessors extends SubDependencyFactory {
        private final GooglePlayServicesLibraryAccessors laccForGooglePlayServicesLibraryAccessors = new GooglePlayServicesLibraryAccessors(owner);

        public GooglePlayLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>google.play.services</b>
         */
        public GooglePlayServicesLibraryAccessors getServices() {
            return laccForGooglePlayServicesLibraryAccessors;
        }

    }

    public static class GooglePlayServicesLibraryAccessors extends SubDependencyFactory {

        public GooglePlayServicesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>auth</b> with <b>com.google.android.gms:play-services-auth</b> coordinates and
         * with version reference <b>google.play.services.auth</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAuth() {
            return create("google.play.services.auth");
        }

    }

    public static class GordonwongLibraryAccessors extends SubDependencyFactory {
        private final GordonwongMaterialLibraryAccessors laccForGordonwongMaterialLibraryAccessors = new GordonwongMaterialLibraryAccessors(owner);

        public GordonwongLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>gordonwong.material</b>
         */
        public GordonwongMaterialLibraryAccessors getMaterial() {
            return laccForGordonwongMaterialLibraryAccessors;
        }

    }

    public static class GordonwongMaterialLibraryAccessors extends SubDependencyFactory {
        private final GordonwongMaterialSheetLibraryAccessors laccForGordonwongMaterialSheetLibraryAccessors = new GordonwongMaterialSheetLibraryAccessors(owner);

        public GordonwongMaterialLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>gordonwong.material.sheet</b>
         */
        public GordonwongMaterialSheetLibraryAccessors getSheet() {
            return laccForGordonwongMaterialSheetLibraryAccessors;
        }

    }

    public static class GordonwongMaterialSheetLibraryAccessors extends SubDependencyFactory {

        public GordonwongMaterialSheetLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>fab</b> with <b>com.gordonwong:material-sheet-fab</b> coordinates and
         * with version reference <b>gordonwong.material.sheet.fab</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getFab() {
            return create("gordonwong.material.sheet.fab");
        }

    }

    public static class KoinLibraryAccessors extends SubDependencyFactory {
        private final KoinAndroidLibraryAccessors laccForKoinAndroidLibraryAccessors = new KoinAndroidLibraryAccessors(owner);

        public KoinLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>koin.android</b>
         */
        public KoinAndroidLibraryAccessors getAndroid() {
            return laccForKoinAndroidLibraryAccessors;
        }

    }

    public static class KoinAndroidLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public KoinAndroidLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>android</b> with <b>io.insert-koin:koin-android</b> coordinates and
         * with version reference <b>koin</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("koin.android");
        }

        /**
         * Dependency provider for <b>compat</b> with <b>io.insert-koin:koin-android-compat</b> coordinates and
         * with version reference <b>koin</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCompat() {
            return create("koin.android.compat");
        }

    }

    public static class KotlinLibraryAccessors extends SubDependencyFactory {

        public KotlinLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>stdlib</b> with <b>org.jetbrains.kotlin:kotlin-stdlib</b> coordinates and
         * with version reference <b>kotlin</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getStdlib() {
            return create("kotlin.stdlib");
        }

    }

    public static class KotlinxLibraryAccessors extends SubDependencyFactory {
        private final KotlinxCoroutinesLibraryAccessors laccForKotlinxCoroutinesLibraryAccessors = new KotlinxCoroutinesLibraryAccessors(owner);

        public KotlinxLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>kotlinx.coroutines</b>
         */
        public KotlinxCoroutinesLibraryAccessors getCoroutines() {
            return laccForKotlinxCoroutinesLibraryAccessors;
        }

    }

    public static class KotlinxCoroutinesLibraryAccessors extends SubDependencyFactory {

        public KotlinxCoroutinesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>android</b> with <b>org.jetbrains.kotlinx:kotlinx-coroutines-android</b> coordinates and
         * with version reference <b>coroutines</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAndroid() {
            return create("kotlinx.coroutines.android");
        }

        /**
         * Dependency provider for <b>core</b> with <b>org.jetbrains.kotlinx:kotlinx-coroutines-core</b> coordinates and
         * with version reference <b>coroutines</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("kotlinx.coroutines.core");
        }

    }

    public static class KyleduoLibraryAccessors extends SubDependencyFactory {

        public KyleduoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>switchbutton</b> with <b>com.kyleduo.switchbutton:library</b> coordinates and
         * with version reference <b>kyleduo.switchbutton</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSwitchbutton() {
            return create("kyleduo.switchbutton");
        }

    }

    public static class RoomLibraryAccessors extends SubDependencyFactory {

        public RoomLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>compiler</b> with <b>androidx.room:room-compiler</b> coordinates and
         * with version reference <b>room</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCompiler() {
            return create("room.compiler");
        }

        /**
         * Dependency provider for <b>ktx</b> with <b>androidx.room:room-ktx</b> coordinates and
         * with version reference <b>room</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKtx() {
            return create("room.ktx");
        }

        /**
         * Dependency provider for <b>runtime</b> with <b>androidx.room:room-runtime</b> coordinates and
         * with version reference <b>room</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRuntime() {
            return create("room.runtime");
        }

        /**
         * Dependency provider for <b>testing</b> with <b>androidx.room:room-testing</b> coordinates and
         * with version reference <b>room</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTesting() {
            return create("room.testing");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final AkexorcistVersionAccessors vaccForAkexorcistVersionAccessors = new AkexorcistVersionAccessors(providers, config);
        private final AndroidxVersionAccessors vaccForAndroidxVersionAccessors = new AndroidxVersionAccessors(providers, config);
        private final CkettiVersionAccessors vaccForCkettiVersionAccessors = new CkettiVersionAccessors(providers, config);
        private final FirebaseVersionAccessors vaccForFirebaseVersionAccessors = new FirebaseVersionAccessors(providers, config);
        private final GithubVersionAccessors vaccForGithubVersionAccessors = new GithubVersionAccessors(providers, config);
        private final GoogleVersionAccessors vaccForGoogleVersionAccessors = new GoogleVersionAccessors(providers, config);
        private final GordonwongVersionAccessors vaccForGordonwongVersionAccessors = new GordonwongVersionAccessors(providers, config);
        private final KyleduoVersionAccessors vaccForKyleduoVersionAccessors = new KyleduoVersionAccessors(providers, config);
        private final SwitchVersionAccessors vaccForSwitchVersionAccessors = new SwitchVersionAccessors(providers, config);
        private final ZcwengVersionAccessors vaccForZcwengVersionAccessors = new ZcwengVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>appcompat</b> with value <b>1.7.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAppcompat() { return getVersion("appcompat"); }

        /**
         * Version alias <b>billing</b> with value <b>8.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBilling() { return getVersion("billing"); }

        /**
         * Version alias <b>cardview</b> with value <b>1.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCardview() { return getVersion("cardview"); }

        /**
         * Version alias <b>constraintlayout</b> with value <b>2.2.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getConstraintlayout() { return getVersion("constraintlayout"); }

        /**
         * Version alias <b>coroutines</b> with value <b>1.10.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCoroutines() { return getVersion("coroutines"); }

        /**
         * Version alias <b>credentials</b> with value <b>1.5.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCredentials() { return getVersion("credentials"); }

        /**
         * Version alias <b>googleid</b> with value <b>1.1.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGoogleid() { return getVersion("googleid"); }

        /**
         * Version alias <b>junit</b> with value <b>4.13.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJunit() { return getVersion("junit"); }

        /**
         * Version alias <b>koin</b> with value <b>4.1.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKoin() { return getVersion("koin"); }

        /**
         * Version alias <b>kotlin</b> with value <b>2.2.21</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKotlin() { return getVersion("kotlin"); }

        /**
         * Version alias <b>material</b> with value <b>1.13.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMaterial() { return getVersion("material"); }

        /**
         * Version alias <b>recyclerview</b> with value <b>1.4.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRecyclerview() { return getVersion("recyclerview"); }

        /**
         * Version alias <b>room</b> with value <b>2.8.4</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRoom() { return getVersion("room"); }

        /**
         * Version alias <b>tracing</b> with value <b>1.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTracing() { return getVersion("tracing"); }

        /**
         * Group of versions at <b>versions.akexorcist</b>
         */
        public AkexorcistVersionAccessors getAkexorcist() {
            return vaccForAkexorcistVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.androidx</b>
         */
        public AndroidxVersionAccessors getAndroidx() {
            return vaccForAndroidxVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.cketti</b>
         */
        public CkettiVersionAccessors getCketti() {
            return vaccForCkettiVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.firebase</b>
         */
        public FirebaseVersionAccessors getFirebase() {
            return vaccForFirebaseVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.github</b>
         */
        public GithubVersionAccessors getGithub() {
            return vaccForGithubVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.google</b>
         */
        public GoogleVersionAccessors getGoogle() {
            return vaccForGoogleVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.gordonwong</b>
         */
        public GordonwongVersionAccessors getGordonwong() {
            return vaccForGordonwongVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.kyleduo</b>
         */
        public KyleduoVersionAccessors getKyleduo() {
            return vaccForKyleduoVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.switch</b>
         */
        public SwitchVersionAccessors getSwitch() {
            return vaccForSwitchVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.zcweng</b>
         */
        public ZcwengVersionAccessors getZcweng() {
            return vaccForZcwengVersionAccessors;
        }

    }

    public static class AkexorcistVersionAccessors extends VersionFactory  {

        public AkexorcistVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>akexorcist.roundcornerprogressbar</b> with value <b>2.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRoundcornerprogressbar() { return getVersion("akexorcist.roundcornerprogressbar"); }

    }

    public static class AndroidxVersionAccessors extends VersionFactory  {

        private final AndroidxDatastoreVersionAccessors vaccForAndroidxDatastoreVersionAccessors = new AndroidxDatastoreVersionAccessors(providers, config);
        private final AndroidxEspressoVersionAccessors vaccForAndroidxEspressoVersionAccessors = new AndroidxEspressoVersionAccessors(providers, config);
        private final AndroidxTestVersionAccessors vaccForAndroidxTestVersionAccessors = new AndroidxTestVersionAccessors(providers, config);
        public AndroidxVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.androidx.datastore</b>
         */
        public AndroidxDatastoreVersionAccessors getDatastore() {
            return vaccForAndroidxDatastoreVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.androidx.espresso</b>
         */
        public AndroidxEspressoVersionAccessors getEspresso() {
            return vaccForAndroidxEspressoVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.androidx.test</b>
         */
        public AndroidxTestVersionAccessors getTest() {
            return vaccForAndroidxTestVersionAccessors;
        }

    }

    public static class AndroidxDatastoreVersionAccessors extends VersionFactory  {

        public AndroidxDatastoreVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>androidx.datastore.preferences</b> with value <b>1.2.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPreferences() { return getVersion("androidx.datastore.preferences"); }

    }

    public static class AndroidxEspressoVersionAccessors extends VersionFactory  {

        public AndroidxEspressoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>androidx.espresso.core</b> with value <b>3.7.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCore() { return getVersion("androidx.espresso.core"); }

    }

    public static class AndroidxTestVersionAccessors extends VersionFactory  {

        private final AndroidxTestExtVersionAccessors vaccForAndroidxTestExtVersionAccessors = new AndroidxTestExtVersionAccessors(providers, config);
        public AndroidxTestVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.androidx.test.ext</b>
         */
        public AndroidxTestExtVersionAccessors getExt() {
            return vaccForAndroidxTestExtVersionAccessors;
        }

    }

    public static class AndroidxTestExtVersionAccessors extends VersionFactory  {

        public AndroidxTestExtVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>androidx.test.ext.junit</b> with value <b>1.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJunit() { return getVersion("androidx.test.ext.junit"); }

    }

    public static class CkettiVersionAccessors extends VersionFactory  {

        private final CkettiEmailVersionAccessors vaccForCkettiEmailVersionAccessors = new CkettiEmailVersionAccessors(providers, config);
        public CkettiVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.cketti.email</b>
         */
        public CkettiEmailVersionAccessors getEmail() {
            return vaccForCkettiEmailVersionAccessors;
        }

    }

    public static class CkettiEmailVersionAccessors extends VersionFactory  {

        private final CkettiEmailIntentVersionAccessors vaccForCkettiEmailIntentVersionAccessors = new CkettiEmailIntentVersionAccessors(providers, config);
        public CkettiEmailVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.cketti.email.intent</b>
         */
        public CkettiEmailIntentVersionAccessors getIntent() {
            return vaccForCkettiEmailIntentVersionAccessors;
        }

    }

    public static class CkettiEmailIntentVersionAccessors extends VersionFactory  {

        public CkettiEmailIntentVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>cketti.email.intent.builder</b> with value <b>2.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBuilder() { return getVersion("cketti.email.intent.builder"); }

    }

    public static class FirebaseVersionAccessors extends VersionFactory  {

        private final FirebaseUiVersionAccessors vaccForFirebaseUiVersionAccessors = new FirebaseUiVersionAccessors(providers, config);
        public FirebaseVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>firebase.bom</b> with value <b>34.6.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBom() { return getVersion("firebase.bom"); }

        /**
         * Group of versions at <b>versions.firebase.ui</b>
         */
        public FirebaseUiVersionAccessors getUi() {
            return vaccForFirebaseUiVersionAccessors;
        }

    }

    public static class FirebaseUiVersionAccessors extends VersionFactory  {

        public FirebaseUiVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>firebase.ui.auth</b> with value <b>9.1.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAuth() { return getVersion("firebase.ui.auth"); }

    }

    public static class GithubVersionAccessors extends VersionFactory  {

        private final GithubArimortyVersionAccessors vaccForGithubArimortyVersionAccessors = new GithubArimortyVersionAccessors(providers, config);
        private final GithubYbqVersionAccessors vaccForGithubYbqVersionAccessors = new GithubYbqVersionAccessors(providers, config);
        private final GithubZurcheVersionAccessors vaccForGithubZurcheVersionAccessors = new GithubZurcheVersionAccessors(providers, config);
        public GithubVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.github.arimorty</b>
         */
        public GithubArimortyVersionAccessors getArimorty() {
            return vaccForGithubArimortyVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.github.ybq</b>
         */
        public GithubYbqVersionAccessors getYbq() {
            return vaccForGithubYbqVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.github.zurche</b>
         */
        public GithubZurcheVersionAccessors getZurche() {
            return vaccForGithubZurcheVersionAccessors;
        }

    }

    public static class GithubArimortyVersionAccessors extends VersionFactory  {

        public GithubArimortyVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>github.arimorty.floatingsearchview</b> with value <b>2.1.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFloatingsearchview() { return getVersion("github.arimorty.floatingsearchview"); }

    }

    public static class GithubYbqVersionAccessors extends VersionFactory  {

        private final GithubYbqAndroidVersionAccessors vaccForGithubYbqAndroidVersionAccessors = new GithubYbqAndroidVersionAccessors(providers, config);
        public GithubYbqVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.github.ybq.android</b>
         */
        public GithubYbqAndroidVersionAccessors getAndroid() {
            return vaccForGithubYbqAndroidVersionAccessors;
        }

    }

    public static class GithubYbqAndroidVersionAccessors extends VersionFactory  {

        public GithubYbqAndroidVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>github.ybq.android.spinkit</b> with value <b>1.2.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpinkit() { return getVersion("github.ybq.android.spinkit"); }

    }

    public static class GithubZurcheVersionAccessors extends VersionFactory  {

        private final GithubZurchePlainVersionAccessors vaccForGithubZurchePlainVersionAccessors = new GithubZurchePlainVersionAccessors(providers, config);
        public GithubZurcheVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.github.zurche.plain</b>
         */
        public GithubZurchePlainVersionAccessors getPlain() {
            return vaccForGithubZurchePlainVersionAccessors;
        }

    }

    public static class GithubZurchePlainVersionAccessors extends VersionFactory  {

        public GithubZurchePlainVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>github.zurche.plain.pie</b> with value <b>v0.2.4</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPie() { return getVersion("github.zurche.plain.pie"); }

    }

    public static class GoogleVersionAccessors extends VersionFactory  {

        private final GooglePlayVersionAccessors vaccForGooglePlayVersionAccessors = new GooglePlayVersionAccessors(providers, config);
        public GoogleVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.google.play</b>
         */
        public GooglePlayVersionAccessors getPlay() {
            return vaccForGooglePlayVersionAccessors;
        }

    }

    public static class GooglePlayVersionAccessors extends VersionFactory  {

        private final GooglePlayServicesVersionAccessors vaccForGooglePlayServicesVersionAccessors = new GooglePlayServicesVersionAccessors(providers, config);
        public GooglePlayVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.google.play.services</b>
         */
        public GooglePlayServicesVersionAccessors getServices() {
            return vaccForGooglePlayServicesVersionAccessors;
        }

    }

    public static class GooglePlayServicesVersionAccessors extends VersionFactory  {

        public GooglePlayServicesVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>google.play.services.auth</b> with value <b>21.4.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAuth() { return getVersion("google.play.services.auth"); }

    }

    public static class GordonwongVersionAccessors extends VersionFactory  {

        private final GordonwongMaterialVersionAccessors vaccForGordonwongMaterialVersionAccessors = new GordonwongMaterialVersionAccessors(providers, config);
        public GordonwongVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.gordonwong.material</b>
         */
        public GordonwongMaterialVersionAccessors getMaterial() {
            return vaccForGordonwongMaterialVersionAccessors;
        }

    }

    public static class GordonwongMaterialVersionAccessors extends VersionFactory  {

        private final GordonwongMaterialSheetVersionAccessors vaccForGordonwongMaterialSheetVersionAccessors = new GordonwongMaterialSheetVersionAccessors(providers, config);
        public GordonwongMaterialVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.gordonwong.material.sheet</b>
         */
        public GordonwongMaterialSheetVersionAccessors getSheet() {
            return vaccForGordonwongMaterialSheetVersionAccessors;
        }

    }

    public static class GordonwongMaterialSheetVersionAccessors extends VersionFactory  {

        public GordonwongMaterialSheetVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>gordonwong.material.sheet.fab</b> with value <b>1.2.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFab() { return getVersion("gordonwong.material.sheet.fab"); }

    }

    public static class KyleduoVersionAccessors extends VersionFactory  {

        public KyleduoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>kyleduo.switchbutton</b> with value <b>2.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSwitchbutton() { return getVersion("kyleduo.switchbutton"); }

    }

    public static class SwitchVersionAccessors extends VersionFactory  {

        public SwitchVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>switch.button</b> with value <b>0.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getButton() { return getVersion("switch.button"); }

    }

    public static class ZcwengVersionAccessors extends VersionFactory  {

        private final ZcwengSwitchVersionAccessors vaccForZcwengSwitchVersionAccessors = new ZcwengSwitchVersionAccessors(providers, config);
        public ZcwengVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.zcweng.switch</b>
         */
        public ZcwengSwitchVersionAccessors getSwitch() {
            return vaccForZcwengSwitchVersionAccessors;
        }

    }

    public static class ZcwengSwitchVersionAccessors extends VersionFactory  {

        public ZcwengSwitchVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>zcweng.switch.button</b> with value <b>0.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getButton() { return getVersion("zcweng.switch.button"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

        /**
         * Dependency bundle provider for <b>androidTest</b> which contains the following dependencies:
         * <ul>
         *    <li>androidx.test.ext:junit</li>
         *    <li>androidx.test.espresso:espresso-core</li>
         * </ul>
         * <p>
         * This bundle was declared in catalog libs.versions.toml
         */
        public Provider<ExternalModuleDependencyBundle> getAndroidTest() {
            return createBundle("androidTest");
        }

        /**
         * Dependency bundle provider for <b>firebase</b> which contains the following dependencies:
         * <ul>
         *    <li>com.google.firebase:firebase-auth</li>
         *    <li>com.google.firebase:firebase-database</li>
         *    <li>com.google.firebase:firebase-storage</li>
         *    <li>com.google.firebase:firebase-analytics</li>
         *    <li>com.google.firebase:firebase-crashlytics</li>
         *    <li>com.firebaseui:firebase-ui-auth</li>
         * </ul>
         * <p>
         * This bundle was declared in catalog libs.versions.toml
         */
        public Provider<ExternalModuleDependencyBundle> getFirebase() {
            return createBundle("firebase");
        }

        /**
         * Dependency bundle provider for <b>ui</b> which contains the following dependencies:
         * <ul>
         *    <li>androidx.appcompat:appcompat</li>
         *    <li>com.google.android.material:material</li>
         *    <li>androidx.constraintlayout:constraintlayout</li>
         *    <li>androidx.recyclerview:recyclerview</li>
         *    <li>androidx.cardview:cardview</li>
         * </ul>
         * <p>
         * This bundle was declared in catalog libs.versions.toml
         */
        public Provider<ExternalModuleDependencyBundle> getUi() {
            return createBundle("ui");
        }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
