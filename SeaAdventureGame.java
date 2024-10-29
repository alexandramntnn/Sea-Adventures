import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * sea adventure game for CBL.
 * 
 */
public class SeaAdventureGame extends JPanel implements ActionListener {
    // images for pages and game elements
    private Image mainPageImage;
    private Image instructionsPageImage;
    private Image changeSkinPageImage;
    private Image missionsPageImage;
    private Image seaImage;
    private Image playerImage;
    private Image bombImage;
    private Image coinImage;
    private Image sharkImage;
    private Image chestImage;
    private Image magnetImage;
    private Image gameOverImage60Seconds;
    private Image gameOverImageChallenge;
    private Image gameOverImageCompleted;
    private Image pausePlayButtonImage;

    // game variables
    private String currentPage = "main";
    private int coins = 0;
    private int maxCoins = 0;
    private int hearts = 5;
    private int timeLeft = 60;
    private boolean isPaused = false;

    // lists to track game elements
    private List<Rectangle> coinsList = new ArrayList<>();
    private List<Rectangle> bombsList = new ArrayList<>();
    private List<Rectangle> sharksList = new ArrayList<>();
    private List<Rectangle> chestsList = new ArrayList<>();
    private List<Rectangle> magnetsList = new ArrayList<>();

    // player properties
    private int playerY = 300;
    private final int playerX = 100;
    private final int playerSpeed = 8;
    private boolean inTopHalf = false;
    private static Image currentPlayerSkin;

    // background properties
    private int backgroundX = 0;
    private final int backgroundSpeed = 4;

    // timers
    private Timer timer;
    private Timer countdownTimer;
    private Timer magnetTimer;
    private Timer speedIncrementTimer;

    // items properties
    private boolean magnetActive = false;
    private int collectedBombs = 0;
    private int collectedSharks = 0;
    private int collectedChests = 0;
    private int collectedMagnets = 0;
    private int collectedCoins = 0;

    // skin button bounds
    private Rectangle skin1Bounds = new Rectangle(100, 100, 210, 200);
    private Rectangle skin2Bounds = new Rectangle(470, 100, 200, 210);
    private Rectangle skin3Bounds = new Rectangle(110, 350, 200, 210);
    private Rectangle skin4Bounds = new Rectangle(470, 350, 210, 210);

    // mission button bounds
    Rectangle mission1Bounds = new Rectangle(150, 250, 200, 100);
    Rectangle mission2Bounds = new Rectangle(450, 250, 200, 100);

    // game actions buttons bounds
    private Rectangle playAgainButtonBounds = new Rectangle(98, 403, 270, 55);
    private Rectangle homeButtonBounds = new Rectangle(470, 406, 204, 57);
    private Rectangle missionHomeButtonBounds = new Rectangle(300, 396, 201, 52);
    private Rectangle challengeHomeButtonBounds = new Rectangle(110, 388, 190, 64);
    private Rectangle nextChallengeButtonBounds = new Rectangle(445, 394, 200, 54);
    private Rectangle pausePlayButtonBounds = new Rectangle(350, 10, 100, 50);
    private Rectangle playButtonBounds = new Rectangle(277, 305, 254, 57);
    private Rectangle instructionsButtonBounds = new Rectangle(123, 408, 216, 47);
    private Rectangle changeSkinButtonBounds = new Rectangle(469, 405, 214, 51);

    // player box for collision detection
    Rectangle playerBounds = new Rectangle(playerX + 20, playerY + 20, 60, 60);

    // mission challenges
    private String[] challenges = {
        "Collect 30 coins",
        "Survive for 60 seconds",
        "Collect 5 chests",
        "Avoid all bombs for 40 seconds",
        "Collect 3 magnets",
        "Collect 20 coins without hitting a bomb",
        "Stay in the top half of the screen for 30 seconds",
        "Collect 4 chests and avoid sharks",
        "Collect 2 magnets"
    };

    // challenge variables
    private String currentChallenge;
    private boolean challengeComplete = false;
    private List<String> completedChallenges = new ArrayList<>();

    // mission speed properties
    private final int missionPlayerSpeed = 8;
    private final int missionBackgroundSpeed = 4;
    private int currentPlayerSpeed;
    private int currentBackgroundSpeed;

    // random variable
    private Random random = new Random();

    /**
     * Constructor for the SeaAdventureGame class.
     * initializes the game panel and loads images.
     */
    public SeaAdventureGame() {
        
        // set the size of the game panel
        setPreferredSize(new Dimension(800, 600));
        
        // allows the panel to receive keyboard input
        setFocusable(true);

        // load images for different game components
        mainPageImage = new ImageIcon("images/start_image.png").getImage();
        instructionsPageImage = new ImageIcon("images/rules.png").getImage();
        changeSkinPageImage = new ImageIcon("images/skins.png").getImage();
        missionsPageImage = new ImageIcon("images/mission.png").getImage();
        seaImage = new ImageIcon("images/sea.png").getImage();
        bombImage = new ImageIcon("images/bomb.png").getImage();
        coinImage = new ImageIcon("images/coin.png").getImage();
        sharkImage = new ImageIcon("images/shark.png").getImage();
        chestImage = new ImageIcon("images/chest.png").getImage();
        magnetImage = new ImageIcon("images/magnet.png").getImage();
        gameOverImage60Seconds = new ImageIcon("images/game_over.png").getImage();
        gameOverImageChallenge = new ImageIcon("images/game_over_home.png").getImage();
        gameOverImageCompleted = new ImageIcon("images/game_over_completed.png").getImage();
        pausePlayButtonImage = new ImageIcon("images/pause_button.png").getImage();

        // set the default player skin
        if (currentPlayerSkin == null) {
            currentPlayerSkin = new ImageIcon("images/1.png").getImage();
        }

        // set player image to the selected skin
        playerImage = currentPlayerSkin;

        // add mouse listener for handling mouse clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        // create a timer to run the game loop, after 16 secs
        timer = new Timer(16, this);
    }

    // handles mouse clicks based on the current page
    private void handleMouseClick(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // determine action based on current page
        switch (currentPage) {
            // selecttion from mainpage
            case "main":
                if (playButtonBounds.contains(x, y)) {
                    currentPage = "missions";
                    repaint();
                } else if (instructionsButtonBounds.contains(x, y)) {
                    currentPage = "instructions";
                    repaint();
                } else if (changeSkinButtonBounds.contains(x, y)) {
                    currentPage = "changeSkin";
                    repaint();
                }
                break;
            // selection from instructions page
            case "instructions":
                // return to main page from instructions page
                currentPage = "main";
                repaint();
                break;

            //selection from missions page
            case "missions":
                // picking mission type
                if (mission1Bounds.contains(x, y)) {
                    currentPage = "game";
                    start60SecondChallenge();
                } else if (mission2Bounds.contains(x, y)) {
                    currentPage = "game";
                    startMissionChallenge();
                }
                break;

            //selection from skins page
            case "changeSkin":
                // handle clicks for changing skins
                if (skin1Bounds.contains(x, y)) {
                    currentPlayerSkin = new ImageIcon("images/1.png").getImage();
                    playerImage = currentPlayerSkin;
                    currentPage = "main";
                    repaint();
                } else if (skin2Bounds.contains(x, y)) {
                    currentPlayerSkin = new ImageIcon("images/2.png").getImage();
                    playerImage = currentPlayerSkin;
                    currentPage = "main";
                    repaint();
                } else if (skin3Bounds.contains(x, y)) {
                    currentPlayerSkin = new ImageIcon("images/3.png").getImage();
                    playerImage = currentPlayerSkin;
                    currentPage = "main";
                    repaint();
                } else if (skin4Bounds.contains(x, y)) {
                    currentPlayerSkin = new ImageIcon("images/4.png").getImage();
                    playerImage = currentPlayerSkin;
                    currentPage = "main";
                    repaint();
                } else {
                    // click outside skin bounds
                    currentPage = "main";
                    repaint();
                }
                break;
            // selection from challenge completescreen
            case "challengeComplete":
                // handle completion screen buttons
                if (challengeHomeButtonBounds.contains(x, y)) {
                    currentPage = "main";
                    repaint();
                } else if (nextChallengeButtonBounds.contains(x, y)) {
                    currentPage = "game";
                    selectNextChallenge();
                    startNextChallenge();
                    repaint();
                }
                break;

            // selection from gameover screen
            case "gameOver":
                // handle game over screen based on challenge type
                if (currentChallenge == null) {
                    if (playAgainButtonBounds.contains(x, y)) {
                        currentPage = "game";
                        start60SecondChallenge();
                        repaint();
                    } else if (homeButtonBounds.contains(x, y)) {
                        currentPage = "main";
                        repaint();
                    }
                } else {
                    if (missionHomeButtonBounds.contains(x, y)) {
                        currentPage = "main";
                        repaint();
                    }
                }
                break;

            // selection from game screen
            case "game":
                // handle clicks on pause/play button
                if (pausePlayButtonBounds.contains(x, y)) {
                    if (isPaused) {
                        resumeGame();
                    } else {
                        pauseGame();
                    }
                    repaint();
                }
                break;
            default:
                break;
        }
    }

    //stops timers, speed, and loop
    private void pauseGame() {
        isPaused = true;
        timer.stop();
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        if (speedIncrementTimer != null) {
            speedIncrementTimer.stop();
        }
    }


    // resumes the game by starting timers, speed incrementer and game loop
    private void resumeGame() {
        isPaused = false;
        timer.start();
        if (countdownTimer != null) {
            countdownTimer.start();
        }
        if (speedIncrementTimer != null && currentChallenge == null) {
            speedIncrementTimer.start();
        }
    }


    // increases the speed of the game
    private void increaseGameSpeed() {
        currentBackgroundSpeed += 1;
        currentPlayerSpeed += 1;
    }

    // initialize for a 60-second timed challenge.
    private void start60SecondChallenge() {

        // sets variables for challenge
        coins = 0;
        hearts = 5;
        timeLeft = 60;
        resetGameElements();
        resetCounters();

        // set initial player and background speed for the challenge
        currentPlayerSpeed = playerSpeed;
        currentBackgroundSpeed = backgroundSpeed;

        // start the countdown timer for 60 seconds
        startCountdownTimer();

        // set up new speed increment timer, increasing speed every 10 seconds
        if (speedIncrementTimer != null) {
            speedIncrementTimer.stop();
        }
        speedIncrementTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increaseGameSpeed();
            }
        });
        speedIncrementTimer.start();

        // add key listener for player movement and start game timer
        addKeyListener(new PlayerKeyAdapter());
        timer.start();
    }

    
    // initialize for a mission-based challenge. 
    private void startMissionChallenge() {
        // variables for mission challenge
        coins = 0;
        hearts = 5;
        timeLeft = 0;
        resetGameElements();
        resetCounters();

        // set constant speed for the mission challenge
        currentPlayerSpeed = missionPlayerSpeed;
        currentBackgroundSpeed = missionBackgroundSpeed;

        // stop any existing speed increment timer
        if (speedIncrementTimer != null) {
            speedIncrementTimer.stop();
            speedIncrementTimer = null;
        }

        // clear the list of completed challenges and choose the next mission
        completedChallenges.clear();
        selectNextChallenge();

        // add key listener for player movement and start game timer
        addKeyListener(new PlayerKeyAdapter());
        timer.start();
    }

    
    // resets the counters for all items. 
    private void resetCounters() {
        collectedBombs = 0;
        collectedSharks = 0;
        collectedChests = 0;
        collectedMagnets = 0;
        collectedCoins = 0;
    }

    //selects the next challenge
    private void selectNextChallenge() {
        List<String> availableChallenges = new ArrayList<>();

        // add all uncompleted challenges to the available challenges list
        for (String challenge : challenges) {
            if (!completedChallenges.contains(challenge)) {
                availableChallenges.add(challenge);
            }
        }

        // if all challenges are completed, end the game
        if (availableChallenges.isEmpty()) {
            currentPage = "gameOver";
            endGame();
        } else {
            // randomly select a challenge to be the next one
            currentChallenge = availableChallenges.get(random.nextInt(availableChallenges.size()));
            challengeComplete = false;

            // initialize elements of game based on the selected challenge
            if (currentChallenge.equals("Survive for 60 seconds")) {
                timeLeft = 60;
                startCountdownTimer();
            } else if (currentChallenge.equals("Avoid all bombs for 40 seconds")) {
                timeLeft = 40;
                startCountdownTimer();
                collectedBombs = 0;
            } else if (currentChallenge.equals(
                    "Stay in the top half of the screen for 30 seconds")) {
                inTopHalf = false;
                timeLeft = 30;
            }

            // reset player health and clear game elements
            hearts = 5;
            resetGameElements();
        }
    }

    //check if game has been completed then exit
    private void checkChallengeCompletion() {
        // exit if there is no active challenge.
        if (currentChallenge == null) {
            return;
        }

        // check if requirements for new challenge have been completed
        switch (currentChallenge) {
            case "Collect 30 coins":
                if (coins >= 30) {
                    completeChallenge();
                    return;
                }
                break;

            case "Survive for 60 seconds":
                if (timeLeft <= 0) {
                    completeChallenge();
                }
                break;

            case "Collect 5 chests":
                if (collectedChests >= 5) {
                    completeChallenge();
                }
                break;

            case "Avoid all bombs for 40 seconds":
                if (timeLeft <= 0) {
                    completeChallenge();
                } else if (collectedBombs > 0) {
                    endGame();
                }
                break;

            case "Collect 3 magnets":
                if (collectedMagnets >= 3) {
                    completeChallenge();
                }
                break;

            case "Collect 20 coins without hitting a bomb":
                if (collectedCoins >= 20 && collectedBombs == 0) {
                    completeChallenge();
                } else if (collectedBombs > 0) {
                    endGame();
                }
                break;

            case "Stay in the top half of the screen for 30 seconds":
                if (playerY < getHeight() / 2) {
                    // start or continue the countdown if the player is in the top half.
                    if (!inTopHalf) {
                        inTopHalf = true;
                        startCountdownTimer();
                    }
                } else {
                    // if player leaves the top half, stop the timer and reset countdown
                    inTopHalf = false;
                    timeLeft = 30;
                    if (countdownTimer != null) {
                        countdownTimer.stop();
                    }
                }
                if (timeLeft <= 0 && inTopHalf) {
                    completeChallenge();
                }
                break;

            case "Collect 4 chests and avoid sharks":
                if (collectedChests >= 4 && collectedSharks == 0) {
                    completeChallenge();
                } else if (collectedSharks > 0) {
                    endGame();
                }
                break;

            case "Collect 2 magnets":
                if (collectedMagnets >= 2) {
                    completeChallenge();
                }
                break;
            default:
                break;
        }
    }


    private void completeChallenge() {
        // mark the current challenge as complete and end the game
        challengeComplete = true;
        completedChallenges.add(currentChallenge);
        endGame();
    }

    private void endGame() {
        // stop all running timers when ending the game
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        timer.stop();

        if (speedIncrementTimer != null) {
            speedIncrementTimer.stop();
        }

        // update max score if current score is higher
        if (coins > maxCoins) {
            maxCoins = coins;
        }

        // set the page to either challenge complete or game over
        currentPage = challengeComplete ? "challengeComplete" : "gameOver";
        repaint();
    }

    private void startNextChallenge() {
        // reset game state
        coins = 0;
        hearts = 5;
        resetGameElements();
        resetCounters();
        addKeyListener(new PlayerKeyAdapter());
        timer.start();

        // set countdown based on the challenge type
        if (currentChallenge.equals("Survive for 60 seconds")) {
            timeLeft = 60;
            startCountdownTimer();
        } else if (currentChallenge.equals("Avoid all bombs for 40 seconds")) {
            timeLeft = 40;
            startCountdownTimer();
        }
    }

    private void startCountdownTimer() {
        // start the countdown timer
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timeLeft--;
                    repaint();
                } else {
                    countdownTimer.stop();
                    // check completion
                    if (currentChallenge != null) {
                        if (currentChallenge.equals(
                            "Stay in the top half of the screen for 30 seconds")
                                && inTopHalf) {
                            completeChallenge();
                        } else {
                            challengeComplete = true;
                            endGame();
                        }
                    } else {
                        endGame();
                    }
                }
            }
        });
        countdownTimer.start();
    }

    private void resetGameElements() {
        // clear game items
        coinsList.clear();
        bombsList.clear();
        sharksList.clear();
        chestsList.clear();
        magnetsList.clear();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (currentPage) {
            case "main":
                // draw the main page
                g.drawImage(mainPageImage, 0, 0, getWidth(), getHeight(), this);
                break;

            case "instructions":
                // draw the instructions page
                g.drawImage(instructionsPageImage, 0, 0, getWidth(), getHeight(), this);
                break;

            case "changeSkin":
                // draw the skin page
                g.drawImage(changeSkinPageImage, 0, 0, getWidth(), getHeight(), this);
                break;

            case "missions":
                // draw the missions page
                g.drawImage(missionsPageImage, 0, 0, getWidth(), getHeight(), this);
                break;

            case "game":
                // draw the game screen elements
                drawGame(g);
                // draw the pause/play button
                g.drawImage(pausePlayButtonImage, 
                    pausePlayButtonBounds.x, pausePlayButtonBounds.y, 
                    pausePlayButtonBounds.width, pausePlayButtonBounds.height, this);
                break;

            case "gameOver":
                // draw the game over screen
                drawGameOverScreen(g);
                break;

            case "challengeComplete":
                // draw the challenge complete screen
                drawChallengeCompleteScreen(g);
                break;
            default:
                break;
        }
    }

    private void drawGame(Graphics g) {
        // draw the moving sea background
        if (seaImage != null) {
            g.drawImage(seaImage, backgroundX, 0, getWidth(), getHeight(), this);
            g.drawImage(seaImage, backgroundX + getWidth(), 0, getWidth(), getHeight(), this);
        }

        // draw red line for "Stay in the top half" challenge
        if (currentChallenge != null && currentChallenge.equals(
            "Stay in the top half of the screen for 30 seconds")) {
            g.setColor(Color.RED);
            int lineY = getHeight() / 2 + 50;
            g.drawLine(0, lineY, getWidth(), lineY);
        }

        // draw coins
        for (Rectangle coin : coinsList) {
            g.drawImage(coinImage, coin.x, coin.y, 30, 30, this);
        }

        // draw bombs
        for (Rectangle bomb : bombsList) {
            g.drawImage(bombImage, bomb.x, bomb.y, 50, 50, this);
        }

        // draw chests
        for (Rectangle chest : chestsList) {
            g.drawImage(chestImage, chest.x, chest.y, 60, 60, this);
        }

        // draw magnets
        for (Rectangle magnet : magnetsList) {
            g.drawImage(magnetImage, magnet.x, magnet.y, 50, 50, this);
        }

        // draw sharks
        for (Rectangle shark : sharksList) {
            g.drawImage(sharkImage, shark.x, shark.y, 60, 60, this);
        }

        // draw player
        g.drawImage(playerImage, playerX, playerY, 100, 100, this);

        // draw magnet effect if magnet is active
        if (magnetActive) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillOval(playerX - 100, playerY - 100, 300, 300);
        }

        // draw scoring elements
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Coins: " + coins, 20, 30);
        g.drawString("Hearts: " + hearts, 20, 60);
        g.drawString("Time left: " + timeLeft + "s", getWidth() - 150, 30);

        // draw the current challenge
        if (currentChallenge != null) {
            g.drawString("Challenge: " + currentChallenge, 20, 90);
        }
    }
    
    // draw the game over screen
    private void drawGameOverScreen(Graphics g) {

        // draw the "GAME OVER" image based on the challenge type
        if (currentChallenge == null) {
            g.drawImage(gameOverImage60Seconds, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.drawImage(gameOverImageChallenge, 0, 0, getWidth(), getHeight(), this);
        }

        // draw current score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Score: " + coins, getWidth() / 2 - 100, getHeight() / 2 - 40);

        // draw the maximum score
        g.drawString("Best score: " + maxCoins, getWidth() / 2 - 100, getHeight() / 2);
    }

    // draw the challenge complete screen
    private void drawChallengeCompleteScreen(Graphics g) {
        // draw the "CHALLENGE COMPLETE" image
        g.drawImage(gameOverImageCompleted, 0, 0, getWidth(), getHeight(), this);

        // draw the player's current score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Score: " + coins, getWidth() / 2 - 90, getHeight() / 2 + 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentPage.equals("game")) {
            // move background continuously
            backgroundX -= currentBackgroundSpeed;
            if (backgroundX <= -getWidth()) {
                backgroundX = 0;
            }

            // define Y boundaries for element spawning
            int minY = 177;
            int maxY = getHeight() - 100;

            // randomly spawn new elements
            if (random.nextInt(100) < 3) { // 3% chance to spawn a coin
                int coinY = minY + random.nextInt(maxY - minY);
                coinsList.add(new Rectangle(getWidth(), coinY, 30, 30));
            }
            if (random.nextInt(100) < 2) { // 2% chance to spawn a bomb
                int bombY = minY + random.nextInt(maxY - minY);
                bombsList.add(new Rectangle(getWidth(), bombY, 50, 50));
            }
            if (random.nextInt(200) < 1) { // 0.5% chance to spawn a shark
                int sharkY = minY + random.nextInt(maxY - minY);
                sharksList.add(new Rectangle(getWidth(), sharkY, 60, 60));
            }
            if (random.nextInt(400) < 1) { // 0.25% chance to spawn a chest
                int chestY = minY + random.nextInt(maxY - minY);
                chestsList.add(new Rectangle(getWidth(), chestY, 60, 60));
            }
            if (random.nextInt(500) < 1) { // 0.2% chance to spawn a magnet
                int magnetY = minY + random.nextInt(maxY - minY);
                magnetsList.add(new Rectangle(getWidth(), magnetY, 50, 50));
            }

            // move all elements to the left
            for (Rectangle coin : coinsList) {
                coin.x -= currentBackgroundSpeed;
            }
            for (Rectangle bomb : bombsList) {
                bomb.x -= currentBackgroundSpeed;
            }
            for (Rectangle shark : sharksList) {
                shark.x -= currentBackgroundSpeed;
            }
            for (Rectangle chest : chestsList) {
                chest.x -= currentBackgroundSpeed;
            }
            for (Rectangle magnet : magnetsList) {
                magnet.x -= currentBackgroundSpeed;
            }

            // update player bounds for accurate collision detection
            playerBounds = new Rectangle(playerX + 20, playerY + 20, 60, 60);

            // handle coin collection
            List<Rectangle> collectedCoinsList = new ArrayList<>();
            for (Rectangle coin : coinsList) {
                if (playerBounds.intersects(coin)) {
                    coins++;
                    collectedCoins++;
                    collectedCoinsList.add(coin);
                } else if (magnetActive) {
                    double distance = Math.sqrt(
                        Math.pow(coin.x - playerX, 2) + Math.pow(coin.y - playerY, 2));
                    if (distance < 150) {
                        coins++;
                        collectedCoins++;
                        collectedCoinsList.add(coin);
                    }
                }
            }
            coinsList.removeAll(collectedCoinsList);

            // handle bomb collisions
            List<Rectangle> hitBombs = new ArrayList<>();
            for (Rectangle bomb : bombsList) {
                if (playerBounds.intersects(bomb)) {
                    hearts--;
                    hitBombs.add(bomb);
                    collectedBombs++;

                    // end game if no hearts left
                    if (hearts == 0) {
                        endGame();
                        return;
                    }
                }
            }
            bombsList.removeAll(hitBombs);

            // handle shark collisions
            List<Rectangle> hitSharks = new ArrayList<>();
            for (Rectangle shark : sharksList) {
                if (playerBounds.intersects(shark)) {
                    hearts--;
                    hitSharks.add(shark);
                    collectedSharks++;

                    // check challenge for avoiding sharks
                    if (currentChallenge != null && currentChallenge.equals(
                        "Collect 4 chests and avoid sharks")) {
                        endGame();
                        return;
                    }

                    // end game if no hearts left
                    if (hearts == 0) {
                        endGame();
                        return;
                    }
                }
            }
            sharksList.removeAll(hitSharks);

            // handle chest collection
            List<Rectangle> collectedChestsList = new ArrayList<>();
            for (Rectangle chest : chestsList) {
                if (playerBounds.intersects(chest)) {
                    coins += 3;
                    collectedChests++;
                    collectedChestsList.add(chest);
                }
            }
            chestsList.removeAll(collectedChestsList);

            // handle magnet collection
            List<Rectangle> collectedMagnetsList = new ArrayList<>();
            for (Rectangle magnet : magnetsList) {
                if (playerBounds.intersects(magnet)) {
                    collectedMagnets++;
                    collectedMagnetsList.add(magnet);
                    activateMagnetEffect();
                }
            }
            magnetsList.removeAll(collectedMagnetsList);

            // check if any challenge is complete
            checkChallengeCompletion();
            repaint();
        }
    }

    // activates the magnet effect for 10 seconds
    private void activateMagnetEffect() {
        magnetActive = true;

        // start a timer for 10 seconds
        if (magnetTimer != null) {
            magnetTimer.stop();
        }
        magnetTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // deactivate magnet effect after timer ends and stop timer
                magnetActive = false;
                magnetTimer.stop();
            }
        });
        // ensure the timer runs only once
        magnetTimer.setRepeats(false);
        magnetTimer.start();
    }

    // keyboard listener for key presses
    private class PlayerKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (currentPage.equals("game")) {
                if (key == KeyEvent.VK_UP) {
                    // move player up but keep within the top sea limit
                    playerY = Math.max(playerY - currentPlayerSpeed, 177);
                }

                if (key == KeyEvent.VK_DOWN) {
                    // move player down but keep within the bottom boundary
                    playerY = Math.min(playerY + currentPlayerSpeed, getHeight() - 100);
                }
            }
        }
    }

    public static void main(String[] args) {
        // set up the game
        JFrame frame = new JFrame("Sea Adventure");
        SeaAdventureGame gamePanel = new SeaAdventureGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}