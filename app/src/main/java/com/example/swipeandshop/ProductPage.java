package com.example.swipeandshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductPage extends AppCompatActivity {
    List<Product> products;
    RecyclerView productCardList;

    //Product page objects.
    Activity productActivity;
    CardView createProductView;

    int currentLocation = -1;
    String currentUrl = "";
    String currentImagePath = "";

    // Buttons
    Button addProductBtn;
    Button cancelProductBtn;
    Button saveProductBtn;

    // Firebase references.
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseRecyclerOptions<Product> options;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> myAdapter;
    DatabaseReference myRef;
    DatabaseReference allProductRef;
    FirebaseUser user;

    //Image info
    private Uri imageUri;
    private ImageView imageView;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsPage.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        // Product page objects.
        productCardList = findViewById(R.id.dataList); //holds all the users products
        addProductBtn = findViewById(R.id.addProductBtn);
        productActivity = (Activity)this;
        products = new ArrayList<>();

        //Database
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        user = firebaseAuth.getCurrentUser();
        //Gets reference to db area with only users products
        myRef = database.getReference().child("users").child(user.getUid()).child("products");
        //Gets reference to db area where all products are stored.
        allProductRef = database.getReference().child("products");

        //Firebase Storage
        storageRef = storage.getReference();
        imageView = findViewById(R.id.imageView);

        // Create Product View Fields
        cancelProductBtn = findViewById(R.id.cancelButton);
        saveProductBtn = findViewById(R.id.saveButton);
        createProductView = findViewById(R.id.createProductView);
        createProductView.setVisibility(View.GONE);

        options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(myRef,Product.class).build();

        //load data.
        loadData();
        //show data.
        showData();
        //set listeners.
        setButtonListeners();

        // Register input validation
        EditText priceInput = findViewById(R.id.priceInput);
        priceInput.addTextChangedListener(new TextValidator(priceInput) {
            @Override public void validate(TextView textView, String text) {
                float f;
                try {
                    f = Float.parseFloat(text);
                } catch (Exception e) {
                    textView.setError("Price must be in $0.1..$10,000 range");
                    return;
                }
                if (f < 0.1 || f > 10000.)
                    textView.setError("Price must be in $0.1..$10,000 range");
            }
        });

        EditText nameInput = findViewById(R.id.nameInput);
        nameInput.addTextChangedListener(new TextValidator(nameInput) {
            @Override public void validate(TextView textView, String text) {
                if (text == null || text.isEmpty() || text.length() < 5 || text.length() > 14 ) {
                    textView.setError("Name must be no more than 5 and less than 14 characters long");
                }
            }
        });

        EditText descriptionInput = findViewById(R.id.descriptionInput);
        descriptionInput.addTextChangedListener(new TextValidator(descriptionInput) {
            @Override public void validate(TextView textView, String text) {
                String patt = "\r\n|\r|\n";
                if (text == null || text.isEmpty() || text.length() < 10 || text.length() > 40 ||
                text.split(patt).length < 1 || text.split(patt).length > 2 ) {
                    textView.setError("Short description must be 10..40 characters long and 2 lines max");
                }
            }
        });

        EditText descriptionInput2 = findViewById(R.id.descriptionInput2);
        descriptionInput2.addTextChangedListener(new TextValidator(descriptionInput2) {
            @Override public void validate(TextView textView, String text) {
                String patt = "\r\n|\r|\n";
                if (text == null || text.isEmpty() || text.length() < 20 || text.length() > 500 ||
                        text.split(patt).length < 1 || text.split(patt).length > 20 ) {
                    textView.setError("Long description must be 20..500 characters long and 20 lines max");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Start the adapter listener
        myAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the adapter listener
        myAdapter.stopListening();
    }


    /**Open create product view for creating a new product.*/
    public void addOnClick(){
        createProductView.setVisibility(View.VISIBLE);
        EditText name = createProductView.findViewById(R.id.nameInput);
        EditText shortDesc = createProductView.findViewById(R.id.descriptionInput);
        EditText longDesc = createProductView.findViewById(R.id.descriptionInput2);
        EditText price = createProductView.findViewById(R.id.nameInput);
        name.setText("");//clear text boxes
        shortDesc.setText("");
        longDesc.setText("");
        price.setText("");
        addProductBtn.setVisibility(View.GONE);
    }

    /**Cancel the editing or creation of an object.*/
    public void cancelOnClick(){
        createProductView.setVisibility(View.GONE);
        addProductBtn.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.googleg_disabled_color_18);
        this.currentLocation = -1;
    }

    /**Save product. Either adds or edits a product.*/
    public void saveOnClick(){
        Product product;
        EditText name = createProductView.findViewById(R.id.nameInput);
        EditText shortDesc = createProductView.findViewById(R.id.descriptionInput);
        EditText longDesc = createProductView.findViewById(R.id.descriptionInput2);
        EditText price = createProductView.findViewById(R.id.priceInput);

        //Add product to Realtime database
        user = firebaseAuth.getCurrentUser();

        // if currentLocation >= 0 then we are editing an already existing obj.
        // if currentLocation < 0 then create a new object.
        if(currentLocation >= 0){
            product = products.remove(currentLocation);
            if(currentUrl.length() == 0){
                currentUrl = product.getImageUrl();
                currentImagePath = product.getImagePath();
            }

            /*Add data to product.*/
            product.setName(name.getText().toString());
            product.setShortDescription(shortDesc.getText().toString());
            product.setLongDescription(longDesc.getText().toString());
            if(price.getText().length() > 0){
                product.setPrice(Float.parseFloat(price.getText().toString()));
            }
            product.setProductId(product.getProductId());
            product.setSellerId(user.getUid());
            product.setSeller("Seller Username");
            product.setImageUrl(currentUrl);
            product.setImagePath(currentImagePath);
            products.add(0,product);
            myRef.child(product.productId).setValue(product);
            allProductRef.child(product.productId).setValue(product);
        }else{
            if(currentUrl.length() == 0){
                Toast.makeText(ProductPage.this, "You need to add at least one image.", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseReference newRef = myRef.push();
            product = new Product();

            /*Add data to product.*/
            product.setName(name.getText().toString());
            product.setShortDescription(shortDesc.getText().toString());
            product.setLongDescription(longDesc.getText().toString());
            if(price.getText().length() > 0){
                product.setPrice(Float.parseFloat(price.getText().toString()));
            }
            product.setSellerId(user.getUid());
            product.setSeller("Seller Username");
            product.setImageUrl(currentUrl);
            product.setProductId(newRef.getKey());
            product.setImagePath(currentImagePath);

            products.add(product); // add our new product to the list
            newRef.setValue(product);
            allProductRef.child(product.productId).setValue(product);
        }

        //This closes the editing/creation of the product.
        createProductView.setVisibility(View.GONE);
        addProductBtn.setVisibility(View.VISIBLE);

        //sets default image (will be changed to show current product image)
        imageView.setImageResource(R.drawable.googleg_disabled_color_18);
        this.currentLocation = -1;
        this.currentUrl = "";
        this.currentImagePath = "";
        // updating the grid view to include the new product
        productCardList.setAdapter(myAdapter);
    }

    /**Retrieves data from firebase when page is first loaded.*/
    public void loadData(){
        // this will load data from firebase and put it into the products list.
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item_snapshot:snapshot.getChildren()){
                    Product tempProduct = item_snapshot.getValue(Product.class);
                    // make sure user hasn't already swiped on this object.
                    products.add(tempProduct);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    /**Loads data into teh firebaseUI adapter and displays it in the grid layout.*/
    public void showData(){
        // This takes the loaded data from firebase and puts it into the adapter.
        myAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Product product) {
                productViewHolder.title.setText(product.getName());
                //set other info here.
                Picasso.get()
                        .load(product.getImageUrl())
                        .fit()
                        .centerCrop()
                        .into(productViewHolder.gridIcon);
                //productViewHolder.gridIcon.setImageResource();

                //Used to get which item is clicked on in the view.
                productViewHolder.bindData(new ViewHolderCallback(){
                    @Override
                    public void itemWasClicked(int position){
                        // populate the text fields for creating/editing a product.
                        populateCreateProductView(createProductView, position);
                    }

                    @Override
                    public void itemToDelete(int position) {
                        // remove product from database and product list
                        removeProduct(position);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate the view (instantiate the card in the view).
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_layout,parent,false);
                return new ProductViewHolder(view);
            }
        };

        // Set grid to our adapter.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        productCardList.setLayoutManager(gridLayoutManager);
        productCardList.setAdapter(myAdapter);
    }

    /**Fill in the data for the product in each text field if data was already provided.*/
    public void populateCreateProductView(CardView view, int productIndex){
        // Set the create/edit product card visbile
        createProductView.setVisibility(View.VISIBLE);

        // Get references to each text box.
        EditText name = (EditText) view.findViewById(R.id.nameInput);
        EditText description = (EditText) view.findViewById(R.id.descriptionInput);

        // get the product that was clicked on.
        Product product = products.get(productIndex);
        ImageView myImage = view.findViewById(R.id.imageView);
        Picasso.get()
                .load(product.getImageUrl())
                .fit()
                .centerCrop()
                .into(myImage);

        // populate those text boxes.
        name.setText(product.getName());
        description.setText(product.getShortDescription());

        // set the current location of the product in the products list to be used for later.
        this.currentLocation = productIndex;
    }

    public void removeProduct(int position){
        // removed product from products list and store in a temp var.
        Product tempProduct = products.remove(position);
        myRef.child(tempProduct.productId).removeValue();
        allProductRef.child(tempProduct.productId).removeValue();
        removeFromFirebase(tempProduct.getImagePath());
        // update the adapter.
        productCardList.setAdapter(myAdapter);
    }

    /**Set all the button listeners in the view.*/
    public void setButtonListeners(){
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOnClick();
            }
        });

        cancelProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOnClick();
            }
        });

        saveProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOnClick();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens up our photos or Google drive
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            if (imageUri != null){
                Toast.makeText(ProductPage.this, "You have selected an image", Toast.LENGTH_SHORT).show();
                uploadToFirebase(imageUri);
            }else{
                Toast.makeText(ProductPage.this, "Please Select Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadToFirebase(Uri uri){
        String imagePath = System.currentTimeMillis() + "." + getFileExtension(uri);
        final StorageReference fileRef = storageRef.child(imagePath);
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // store url to be used later.
                        currentUrl = uri.toString();
                        currentImagePath = imagePath;
                        Toast.makeText(ProductPage.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        imageUri = null;
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ProductPage.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    /**Remove image from firebase.*/
    private void removeFromFirebase(String path){
        if(path.length() == 0){
            return;
        }
        StorageReference delRef = storageRef.child(path);
        // Delete the file
        delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    public abstract class TextValidator implements TextWatcher {
        private final TextView textView;

        public TextValidator(TextView textView) {
            this.textView = textView;
        }

        public abstract void validate(TextView textView, String text);

        @Override
        final public void afterTextChanged(Editable s) {
            String text = textView.getText().toString();
            validate(textView, text);
        }

        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
    }
}