package com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingViewModelInstrumentedTest {
    private SettingViewModel vm;

    @Before
    public void setup(){
        Context ctx = ApplicationProvider.getApplicationContext();
        vm = new SettingViewModel((android.app.Application) ApplicationProvider.getApplicationContext());
    }

    @Test
    public void soundToggleUpdatesSnapshot(){
        vm.setSound(false);
        SettingsUiState s = vm.getState().getValue();
        assertTrue(s instanceof SettingsUiState.SettingsLoaded);
        SettingsSnapshot snap = ((SettingsUiState.SettingsLoaded) s).snapshot;
        assertFalse(snap.sound);
    }

    @Test
    public void enforceAtLeastOneActive(){
        vm.setIeltsActive(false);
        vm.setToeflActive(false);
        vm.setSatActive(false);
        vm.setGreActive(false);
        SettingsUiState s = vm.getState().getValue();
        assertTrue(s instanceof SettingsUiState.Error);
    }

    @Test
    public void signedInStateCarriesNameAndEmail(){
        vm.onAuthenticated("Name","email@example.com");
        SettingsUiState s = vm.getState().getValue();
        assertTrue(s instanceof SettingsUiState.SignedIn);
        SettingsUiState.SignedIn si = (SettingsUiState.SignedIn) s;
        assertEquals("Name", si.name);
        assertEquals("email@example.com", si.email);
    }
}
