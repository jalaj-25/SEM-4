PImage cart; // Image for the cart
float cart_x, cart_y; //Cart postion
float speed = 5; //speed of cart
float maxBoxH = 4100, maxBoxW = 7000, minBox = 300; //enlarged size of boxes
float difficultyMultiplier = 1.0; //obstacle falling
float rotationAngle = 0; // Angle for rotation
float button_x, button_y, button_w = 180, button_h = 70; //buttons size 
float git_button_y, mail_button_y;
float box_x, box_y, box_w, box_h; //orignal box size
float pole_x, pole_y, pole_z; //pole 3d model
float roadWidth, roadHeight; //road size

boolean gameStarted = false, expandingBox = false; // game and box 
boolean moveLeft = false, moveRight = false, moveUp = false, moveDown = false; //cart movement condition
boolean hideBackground = false; //bg is hiding
boolean obstaclesFalling = false; // obstacles are falling or not

int startTime = 0; //game started time

ArrayList<Obstacle> obstacles = new ArrayList<>(); //obstaclle array

PShape pole; //pole 3d model;
//overlap condition
CertificateBlock certificateBlock; 
AchievementBlock achievementBlock; 

void setup() {
  fullScreen(); 
  fullScreen(P3D);  // Enable 3D rendering
   //screen border
  stroke(#D60DFF);
  strokeWeight(7);
  
    pole = loadShape("pole.obj");  // Load your 3D model (ensure it's in the data folder)
    pole_x = width / 2;
    pole_y = height / 2;
    pole_z = 0;
  
  //cart image call
  cart = loadImage("cart.png");
  if (cart == null) {
    println("Failed to load cart image.");
    exit();
  }
  cart.resize(150, 120);
  //cart position
  cart_x = width / 2;
  cart_y = height / 2;
  //buttons
  button_x = 50;
  button_y = height - 120;
  mail_button_y = button_y - 90;
  git_button_y = mail_button_y - 90;
  
  //box condition
  box_w = minBox;
  box_h = minBox;
  box_x = 50;
  box_y = 50;

  //road size
  roadWidth = width / 5;
  roadHeight = width;

  //CertificateBlock
  certificateBlock = new CertificateBlock(width / 2 - 150, height / 2 - 150, 300, 300);
  //achivement block  
  achievementBlock = new AchievementBlock(width - 350, height / 2 - 150, 300, 300);

}

void draw() {
    if (!hideBackground) { //bg condition
        background(#21EA73); //bg colour

        // Draw road-like structure
        fill(0);
        pushMatrix();
        translate(width / 2, height / 2);
        rotate(PI);
        rect(-roadHeight / 2, -roadWidth / 2, roadHeight, roadWidth);
        popMatrix();

        // "PLAYGROUND" text
        fill(255);
        textSize(40);
        textAlign(CENTER, CENTER);
        text("PLAYGROUND", box_x + box_w / 2, box_y - 30);

        drawButtons();

        // Obstacles logic
        if (gameStarted && millis() - startTime > 2000) {
            obstaclesFalling = true;
            difficultyMultiplier += 0.002;

            if (frameCount % max(1, int(30 / difficultyMultiplier)) == 0) {
                obstacles.add(new Obstacle());
            }
        }

        if (obstaclesFalling) {
            for (int i = obstacles.size() - 1; i >= 0; i--) {
                obstacles.get(i).speed = 5 * difficultyMultiplier;
                obstacles.get(i).move();
                obstacles.get(i).display();

                if (obstacles.get(i).hits(cart_x, cart_y, cart.width, cart.height)) {
                    fill(255, 0, 0);
                    textSize(80);
                    textAlign(CENTER, CENTER);
                    text("GAME OVER!", width / 2, height / 2);
                    noLoop();
                }

                if (obstacles.get(i).y > height) {
                    obstacles.remove(i);
                }
            }
        }
    }
    
    background(#21EA73);

    // Draw road-like structure
    fill(0);
    pushMatrix();
    translate(width / 2, height / 2);
    rotate(PI);
    rect(-roadHeight / 2, -roadWidth / 2, roadHeight, roadWidth);
    popMatrix();

    // "PLAYGROUND" text
    fill(255);
    textSize(40);
    textAlign(CENTER, CENTER);
    text("PLAYGROUND", box_x + box_w / 2, box_y - 30);

    drawButtons();

    // Expand/shrink playground box
    if (expandingBox) {
        box_w = lerp(box_w, maxBoxW, 0.05);
        box_h = lerp(box_h, maxBoxH, 0.05);
    } else {
        box_w = lerp(box_w, minBox, 0.05);
        box_h = lerp(box_h, minBox, 0.05);
    }

    fill(255, 0, 0, 150);
    rect(box_x, box_y, box_w, box_h, 10);

    if (gameStarted) {
        fill(255);
        textSize(50);
        textAlign(CENTER, CENTER);
        text("Protect yourself from obstacles!", width / 2, 100);
    }

    // Certificate logic
    certificateBlock.checkOverlap(cart_x, cart_y, cart.width, cart.height);
    certificateBlock.display();

    // Obstacles logic
    if (gameStarted && millis() - startTime > 2000) {
        obstaclesFalling = true;
        difficultyMultiplier += 0.002;

        if (frameCount % int(30 / difficultyMultiplier) == 0) {
            obstacles.add(new Obstacle());
        }
    }

    if (obstaclesFalling) {
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            obstacles.get(i).speed = 5 * difficultyMultiplier;
            obstacles.get(i).move();
            obstacles.get(i).display();

            if (obstacles.get(i).hits(cart_x, cart_y, cart.width, cart.height)) {
                fill(255, 0, 0);
                textSize(80);
                textAlign(CENTER, CENTER);
                text("GAME OVER!", width / 2, height / 2);
                noLoop();
            }

            if (obstacles.get(i).y > height) {
                obstacles.remove(i);
            }
        }
    }

    // Expand playground when cart is inside
    boolean insidePlayground = (cart_x >= box_x && cart_x + cart.width <= box_x + box_w &&
                                cart_y >= box_y && cart_y + cart.height <= box_y + box_h);

    if (insidePlayground) {
        box_w = lerp(box_w, maxBoxW, 0.02);
        box_h = lerp(box_h, maxBoxH, 0.02);
    } else {
        box_w = lerp(box_w, minBox, 0.02);
        box_h = lerp(box_h, minBox, 0.02);
    }

    achievementBlock.checkOverlap(cart_x, cart_y, cart.width, cart.height);
    achievementBlock.display();

    // ✅ Draw Mart Box (Bottom Right)
    float mart_x = width - 350;  // 50px padding from right
    float mart_y = height - 350; // 50px padding from bottom
    float mart_w = 300, mart_h = 300;

    fill(0, 102, 204);  // Blue color
    rect(mart_x, mart_y, mart_w, mart_h, 15);

    fill(255);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("MART", mart_x + mart_w / 2, mart_y + mart_h / 2);

    // ✅ Draw cart LAST (so it appears on top)
    moveCart(); 
}

void drawButtons() {
  fill(#24292F);
  rect(button_x, git_button_y, button_w, button_h, 15);
  fill(255);
  textSize(24);
  text("GitHub", button_x + button_w / 2, git_button_y + button_h / 2);

  fill(#4285F4);
  rect(button_x, mail_button_y, button_w, button_h, 15);
  fill(255);
  text("Send Email", button_x + button_w / 2, mail_button_y + button_h / 2);

  fill(#FF5733);
  rect(button_x, button_y, button_w, button_h, 15);
  fill(255);
  text("Visit LinkedIn", button_x + button_w / 2, button_y + button_h / 2);
}

// ✅ Cart movement + rotation with diagonal support
void moveCart() {
    float roadLeft = width / 2 - roadHeight / 2;
    float roadRight = width / 2 + roadHeight / 2;

    float dx = 0, dy = 0; // Store movement in x and y directions

    if (moveLeft)  dx -= speed;
    if (moveRight) dx += speed;
    if (moveUp)    dy -= speed;
    if (moveDown)  dy += speed;

    // Normalize diagonal speed (so it's not faster)
    if (dx != 0 && dy != 0) {
        dx *= 0.7;
        dy *= 0.7;
    }

    float newX = cart_x + dx;
    float newY = cart_y + dy;

    if (newX >= roadLeft && newX + cart.width <= roadRight) {
        cart_x = newX;
    }
    if (newY >= 0 && newY + cart.height <= height) {
        cart_y = newY;
    }

    // Set rotation angle based on movement
    if (dx > 0 && dy < 0)       rotationAngle = 45;  // Up-right
    else if (dx > 0 && dy > 0)  rotationAngle = 135; // Down-right
    else if (dx < 0 && dy > 0)  rotationAngle = 225; // Down-left
    else if (dx < 0 && dy < 0)  rotationAngle = 315; // Up-left
    else if (dx > 0)            rotationAngle = 90;  // Right
    else if (dx < 0)            rotationAngle = 270; // Left
    else if (dy < 0)            rotationAngle = 0;   // Up
    else if (dy > 0)            rotationAngle = 180; // Down

    // Draw cart with rotation
    pushMatrix();
    translate(cart_x + cart.width / 2, cart_y + cart.height / 2);
    rotate(radians(rotationAngle));
    imageMode(CENTER);
    image(cart, 0, 0);
    popMatrix();
}


void mousePressed() {
  if (mouseX > cart_x && mouseX < cart_x + cart.width &&
      mouseY > cart_y && mouseY < cart_y + cart.height) {
    rotationAngle = (rotationAngle == 180) ? 270 : 180;
  }
}

// ✅ Certificate block independent class
class CertificateBlock {
    float x, y, w, h;
    float targetW, targetH;

    // ✅ Constructor accepting x, y, w, h
    CertificateBlock(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.targetW = w;
        this.targetH = h;
    }

    void display() {
        // Smoothly transition size changes
        w = lerp(w, targetW, 0.1);
        h = lerp(h, targetH, 0.1);

        // ✅ Keep it centered horizontally and add padding from top
        x = (width - w) / 2;
        y = 50; // 50px padding from top

        // ✅ Draw the rectangle
        fill(100, 100, 255);
        rect(x, y, w, h, 15);

        // ✅ Draw text inside the rectangle
        fill(255);
        textAlign(CENTER, CENTER);
        text("Certificates", x + w / 2, y + h / 2);
    }

    void checkOverlap(float cartX, float cartY, float cartW, float cartH) {
        if (cartX + cartW > x && cartX < x + w &&
            cartY + cartH > y && cartY < y + h) {
            // ✅ Expand on overlap
            targetW = 2100;
            targetH = 1350;
            hideBackground = true;
        } else {
            // ✅ Reset to original size
            targetW = 300;
            targetH = 300;
        }
    }
}

// ✅ Achievement block (same size and behavior as Certificate block)
class AchievementBlock{
    float x, y, w, h;
    float targetW, targetH;

    AchievementBlock(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.targetW = w;
        this.targetH = h;
    }

    void display() {
        // Smoothly transition size changes
        w = lerp(w, targetW, 0.1);
        h = lerp(h, targetH, 0.1);

        // ✅ Positioned at the extreme right
        x = width - w - 50;
        y = 50;

        // ✅ Draw the rectangle
        fill(255, 165, 0); // Orange color
        rect(x, y, w, h, 15);

        // ✅ Draw text inside the rectangle
        fill(0);
        textAlign(CENTER, CENTER);
        textSize(24);
        text("Achievement", x + w / 2, y + h / 2);
    }

    void checkOverlap(float cartX, float cartY, float cartW, float cartH) {
        if (cartX + cartW > x && cartX < x + w &&
            cartY + cartH > y && cartY < y + h) {
            // ✅ Expand on overlap
            targetW = 2100;
            targetH = 1350;
            hideBackground = true;
        } else {
            // ✅ Reset to original size
            targetW = 300;
            targetH = 300;
        }
    }
}


void keyPressed() {
  if (key == 'a') moveLeft = true;
  if (key == 'd') moveRight = true;
  if (key == 'w') moveUp = true;
  if (key == 's') moveDown = true;
}

void keyReleased() {
  moveLeft = moveRight = moveUp = moveDown = false;
}
