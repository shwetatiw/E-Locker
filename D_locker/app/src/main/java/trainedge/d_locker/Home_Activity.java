package trainedge.d_locker;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;


import static trainedge.d_locker.R.id.nav_logout;

public class Home_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "App Sharing";
    public static final int REQUEST_INVITE = 232;
    private MenuItem logout;
    private FloatingActionButton fabscandoc;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase db;
    private DatabaseReference myref;
    List<ScanModel> doumentList;
    private ArrayList<Object> documentList;
    private RecyclerView rvDesc;
    private ProgressBar pb;
    private View headerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        logout = (MenuItem) findViewById(nav_logout);
        fabscandoc = (FloatingActionButton) findViewById(R.id.fabScandoc);
        headerView = navigationView.getHeaderView(0);//header view object for getting image on nav_bar
         //  updateui(headerView);
        fabscandoc.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener =new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null){
                    Intent logint =new Intent(Home_Activity.this,LoginActivity.class);
                    startActivity(logint);
                    finish();
                }
            }
        };
        initGoogleApi();


        //Recycler View Code
        //  pb = (ProgressBar) findViewById(R.id.pbbar);
        db = FirebaseDatabase.getInstance();
        myref = db.getReference();
        //Getting data from firebase code (Recycler view)
        myref = db.getReference("docs_db").child(mAuth.getCurrentUser().getUid());
        //creating blank list in memory
        documentList =new ArrayList<>();
        //Recycler View Object
        final RecyclerView rvDescription= (RecyclerView) findViewById(R.id.rvDescription);
        LinearLayoutManager manager= new LinearLayoutManager(this);
        //passing layput manager
        rvDescription.setLayoutManager(manager);
        //setup Listener
        //using anonymous class
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Data is in data Snapshot obj
                documentList.clear();
                if (dataSnapshot.hasChildren()){
                    //here write datasnapshot.children().{then write iterator and chose for loop then next line automaticaaly genrated}
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        documentList.add(new ScanModel(snapshot));
                    }
                    Toast.makeText(Home_Activity.this, "Data loaded Successfully", Toast.LENGTH_SHORT).show();

                    DocumentAdapter adapter=new DocumentAdapter(documentList);
                    rvDescription.setAdapter(adapter);
                }
                else {
                    Toast.makeText(Home_Activity.this, "Data not Available", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Home_Activity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });






    }

    private void initGoogleApi() {
        //App Sharing code
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_about) {
            Intent setting=new Intent(Home_Activity.this, AboutActivity.class);
            startActivity(setting);
            return  true;

        }
        if (id == R.id.action_setreminder) {
            Intent reminder=new Intent(Home_Activity.this, CalenderActivity.class);
            startActivity(reminder);
            return  true;

        }
        if (id == R.id.action_setreminder) {
            Intent reminder=new Intent(Home_Activity.this, CalenderActivity.class);
            startActivity(reminder);
            return  true;

        }




        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_scan:
                // todo Handle the camera action
                Intent camera = new Intent(Home_Activity.this, ScanningActivity.class);
                startActivity(camera);
                break;




          /*  case R.id.nav_profile:
                //// TODO: get users profile info.

                Intent profileinfo = new Intent(HomeActivity.this,GetProfile.class);
                startActivity(profileinfo);

                break;
*/
            case R.id.nav_invite:
                //// TODO: shareApp
                sendInvitation();
                return true;
            case R.id.nav_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "D_Locker");

                share.putExtra(Intent.EXTRA_TEXT, "Your friend has invited you to join the app./n To join click the link");
                startActivity(Intent.createChooser(share, "Share via..."));
                break;



            case R.id.nav_feedback:
                //Todo feedback and Rate app
                Intent feedbackrate = new Intent(this, FeedbackActivity.class);
                startActivity(feedbackrate);
                break;

            case nav_logout:
                //// TODO: user logout
                mAuth.signOut();
                try {
                    LoginManager.getInstance().logOut();
                    AccessToken.setCurrentAccessToken(null);
                } catch (Exception ignored){
                }
                Intent lgtIntent=new Intent(this,LoginActivity.class);
                startActivity(lgtIntent);

                finish();
                break;
            case R.id.nav_profile:
                //// TODO: get users profile info.

                Intent profileinfo = new Intent(Home_Activity.this, Profile_information_activity.class);
                startActivity(profileinfo);

                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, ScanningActivity.class);
        startActivity(i);
    }

    //App Sharing Code Methods
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }
    //featching profile information in header view-----------------------------------------------------
    private void updateui(View headerView) {
        ImageView imageView = (ImageView) headerView.findViewById(R.id.headerView);
        TextView name = (TextView) headerView.findViewById(R.id.name);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       String username = user.getDisplayName();
        String email1 = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        name.setText(username);
        email.setText(email1);
        Picasso.with(this)
                .load(photoUrl)
                .transform(new CircleTransform())
                .into(imageView);
    }
    private class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


}
