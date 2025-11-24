package com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel;

public interface SettingsUiState {
    final class Loading implements SettingsUiState {}
    final class Error implements SettingsUiState { public final String message; public Error(String m){ this.message = m; } }
    final class SignedIn implements SettingsUiState { public final String name; public final String email; public SignedIn(String n, String e){ this.name = n; this.email = e; } }
    final class SignedOut implements SettingsUiState {}
    final class SettingsLoaded implements SettingsUiState { public final SettingsSnapshot snapshot; public SettingsLoaded(SettingsSnapshot s){ this.snapshot = s; } }
}

