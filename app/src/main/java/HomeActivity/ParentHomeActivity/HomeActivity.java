package HomeActivity.ParentHomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.jspm.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import BottomNavigation.ParentNavigation.ParentAccount;
import BottomNavigation.ParentNavigation.ParentAppLock;
import BottomNavigation.ParentNavigation.ParentListAdapter.GeofenceService;
import BottomNavigation.ParentNavigation.ParentMap;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationViewpar;
    Fragment parentaccount,parentapplock,parentMap;

    public static SharedPreferences lockSharedPreference , iconSharedPreference;
    public static SharedPreferences.Editor lockEditor,iconEditor;
    public static final String PREF_LOCK = "LockStatus";
    public static final String PREF_APP_ICON = "downloadIcon";
    public static Context ParentHomeContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        bottomNavigationViewpar.setSelectedItemId(R.id.nav_parlock);

        switchFragments(new ParentAppLock(),true);
        
        bottomNavigationViewpar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_parlock)
                {
                    switchFragments(parentapplock,false);
                }
                else if (id==R.id.nav_parentAccount)
                {
                  switchFragments(parentaccount,false);
                } else if (id==R.id.nav_parentMap) {
                    switchFragments(parentMap,false);
                }
                return true;
            }
        });
        startGeofenceForground();
    }
    void switchFragments(Fragment fragment,Boolean start){
        if(start)
        {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.parentframelayout, fragment);
            transaction.commit();
        }
        else {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.parentframelayout, fragment);
            transaction.commit();
        }
    }
    void init()
    {
        bottomNavigationViewpar = findViewById(R.id.BottomNavigationViewpar);
        parentaccount = new ParentAccount();
        parentapplock = new ParentAppLock();
        parentMap = new ParentMap();
        ParentHomeContext = HomeActivity.this;
        iconSharedPreference = getSharedPreferences(PREF_APP_ICON,MODE_PRIVATE);
        lockSharedPreference = getSharedPreferences(PREF_LOCK,MODE_PRIVATE);
        lockEditor = lockSharedPreference.edit();
        iconEditor = iconSharedPreference.edit();
    }
    void startGeofenceForground(){
        Intent intent = new Intent(this, GeofenceService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
    }

}