/*
 * Copyright (C) 2019 Benzo Rom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.qs.tiles;

import android.content.ComponentName;
import android.content.Intent;
import android.service.quicksettings.Tile;

import com.android.systemui.custom.onthego.OnTheGoService;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.R;
import com.android.systemui.R.drawable;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.custom.OnTheGoUtils;

import javax.inject.Inject;

/** Quick settings tile: Enable/Disable OnTheGo Mode **/
public class OnTheGoTile extends QSTileImpl<BooleanState> {

    private final Icon mIcon = ResourceIcon.get(drawable.ic_onthego);

    @Inject
    public OnTheGoTile(QSHost host) {
        super(host);
    }

    @Override
    public BooleanState newTileState() {
        BooleanState state = new BooleanState();
        state.handlesLongClick = false;
        return state;
    }

    @Override
    public void handleSetListening(boolean listening) {
        // nothing
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    @Override
    protected void handleClick() {
        toggleService();
        refreshState();
    }

    protected void toggleService() {
        if (isOnTheGoEnabled()) {
            ComponentName cn = new ComponentName("com.android.systemui",
                    "com.android.systemui.custom.onthego.OnTheGoService");
            Intent intent = new Intent();
            intent.setComponent(cn);
            intent.setAction("stop");
            mContext.startService(intent);
        } else {
            getHost().collapsePanels();
            final ComponentName cn = new ComponentName("com.android.systemui",
                    "com.android.systemui.custom.onthego.OnTheGoDialog");
            final Intent intent = new Intent();
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    protected boolean isOnTheGoEnabled() {
        String service = OnTheGoService.class.getName();
        return OnTheGoUtils.isServiceRunning(mContext, service);
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.onthego_label);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        state.value = isOnTheGoEnabled();
        state.state = state.value ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
        state.label = mContext.getString(R.string.onthego_label);
        state.icon = mIcon;
        state.contentDescription = state.label;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.HAVOC_SETTINGS;
    }

    @Override
    protected String composeChangeAnnouncement() {
        if (mState.value) {
            return mContext.getString(
                    R.string.accessibility_quick_settings_onthego_on);
        } else {
            return mContext.getString(
                    R.string.accessibility_quick_settings_onthego_off);
        }
    }
}
