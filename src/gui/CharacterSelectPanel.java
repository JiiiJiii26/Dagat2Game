package gui;

import characters.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * CharacterSelectPanel - Dynamic Tekken-style carousel UI with Overlap & Paginated Modal.
 */
public class CharacterSelectPanel extends JPanel {

    private static final Color OVERLAY_DIM = new Color(0, 0, 0, 200);
    private static final Color SLOT_HOVER  = new Color(255, 255, 255, 40);
    private static final Color TEXT_GOLD   = new Color(255, 215, 0);
    
    private Font customFont;

    private final ArrayList<CharacterData> roster = new ArrayList<>();
    
    // Ocean animation particles (same as MainMenuPanel)
    private final ArrayList<SmokeParticle> smokeList = new ArrayList<>();
    private final ArrayList<SplashParticle> splashList = new ArrayList<>();
    private final ArrayList<CannonFlash> flashList = new ArrayList<>();
    private final Random random = new Random();
    
    private static final int[][] CANNON_POSITIONS = {
        {400, 420}, {520, 400}, {650, 380}, {750, 360}, {900, 370}
    };
    
    private static final int[][] SPLASH_POSITIONS = {
        {150, 500}, {250, 480}, {350, 510}, {800, 460}, 
        {950, 490}, {1100, 470}, {1200, 500}
    };
    
    private final CharacterSelectListener listener;
    private int selectedIndex = -1; 
    private ImageCache imageCache;

    private BufferedImage selectionBg;
    private BufferedImage backBtnImg;
    private BufferedImage deployBtnImg;
    private BufferedImage selectTitleImg; 
    
    private boolean hoverOnBack = false;
    private boolean hoverOnDeploy = false;
    private int hoverSlot = -1;
    
    private boolean showModal = false;
    private boolean hoverOnPageBtn = false;
    private boolean showingSkills = false; 
    private boolean hoverOnInfoBtn = false;

    private static final double BACK_BTN_X = 0.10;
    private static final double BACK_BTN_Y = 0.85;
    private static final double BACK_BTN_W = 0.18;
    private static final double BACK_BTN_H = 0.08;

    private static final double DEPLOY_BTN_X = 0.72;
    private static final double DEPLOY_BTN_Y = 0.85;
    private static final double DEPLOY_BTN_W = 0.18;
    private static final double DEPLOY_BTN_H = 0.08;

    public CharacterSelectPanel(CharacterSelectListener listener) {
        this.listener = listener;
        buildRoster();
        imageCache = new ImageCache(roster);
        imageCache.loadAll();
        
        loadCustomFont();
        loadImages();
        
        setOpaque(false);
        setLayout(null);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverSlot = -1;
                hoverOnBack = false;
                hoverOnDeploy = false;
                showModal = false;
                hoverOnPageBtn = false;
                hoverOnInfoBtn = false;
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e.getPoint());
                repaint();
            }
        });
        
        select(-1);
        
        // Start ocean animation timer
        Timer oceanTimer = new Timer(16, e -> {
            spawnParticles();
            updateParticles();
            repaint();
        });
        oceanTimer.start();
    }

    private void buildRoster() {
        String morganaLore = "Once a naval navigator for the Royal Fleet, Morgana was betrayed by her crew and left to drown during a mutiny. The ocean depths embraced her, transforming her into a mystical siren who now sings to the sea itself. She returns to the surface not for revenge, but to protect the oceans she now calls home.";
        String morganaSkills = "HP: 2100 | Mana: 380\n\nSkill 1: Enchanting Melody\nEffect: Confuses enemy targeting - for 2 turns, enemy sees fake hit/miss indicators.\nMana Cost: 40 | Cooldown: 2 turns\n\nSkill 2: Whirlpool Trap\nEffect: Creates a whirlpool on a 2x2 area - enemy ships entering this area next turn are revealed. (Dmg: 50-100)\nMana Cost: 80 | Cooldown: 3 turns\n\nSkill 3: Storm Call\nEffect: Summons a tempest - floods 4 random enemy cells, making them unusable for 1 turn. (Dmg: 200-350)\nMana Cost: 300 | Cooldown: 4 turns\n\nPassive: Ocean's Embrace\nEffect: First hit each match is automatically dodged.";

        String valeriusLore = "A disgraced military engineer who specialized in coastal fortification. After his city was leveled by a naval bombardment he couldn't stop, Valerius went rogue. He built himself an exoskeleton from the scrap metal of sunken warships. Now, he offers his services to the highest bidder, acting as a one-man fortress that refuses to sink.";
        String valeriusSkills = "HP: 2400 | Mana: 320\n\nSkill 1: Radar Overload\nEffect: Jamming Pulse — Disables the enemy's ability to use any skills for 2 turns.\nMana Cost: 50 | Cooldown: 3 turns\n\nSkill 2: Kinetic Barrier\nEffect: Hardened Hull — Covers a 3x3 area with an energy shield. Shots hit deal 0 damage for 1 turn.\nMana Cost: 90 | Cooldown: 4 turns\n\nSkill 3: Orbital Railgun\nEffect: Precise Strike — Fires a high-velocity slug. Deals massive damage and scratches 4 adjacent cells. (Dmg: 400-600)\nMana Cost: 280 | Cooldown: 5 turns\n\nPassive: Scrapper's Resolve\nEffect: Below 20% HP, gains permanent 10% damage reduction.";

        String kaelLore = "Kael grew up in the storm-ridden coasts where pirates and navy ships constantly clashed. Instead of brute force, he mastered stealth and strategy. Known as the \"Tide Hunter,\" he specializes in hiding fleets and striking enemies before they even know they've been located.";
        String kaelSkills = "HP: 2200 | Energy: 500 | Speed: 85\n\nSkill 1: Silent Drift\nEffect: Hide one boat for 2 turns (cannot be targeted unless revealed).\nEnergy Cost: 80 | Cooldown: 2 turns\n\nSkill 2: Sonar Pulse\nEffect: Reveal one hidden enemy boat for 1 turn. (Dmg: 150-250)\nEnergy Cost: 120 | Cooldown: 3 turns\n\nSkill 3: Depth Charge Barrage\nEffect: If enemy boat is hidden, deals bonus +200 damage. (Dmg: 400-600)\nEnergy Cost: 200 | Cooldown: 4 turns\n\nUltimate: Tempest Lock\nEffect: Hits all enemy boats in a selected area. Cannot miss. (Dmg: 700-900)\nEnergy Cost: 300 | Cooldown: 5 turns";

        String jijiLore = "A lazy child who keeps playing online games in his free time. Procrastinating to the max, he sleeps whenever a task is given. One day, his games gave him the powers to fight the sea. Gifting him the title of TechnoMancer... he's still lazy though.";
        String jijiSkills = "HP: 1950 | Mana: 450\n\nSkill 1: Data Leech\nEffect: Hacks enemy system - reveals 2 random enemy cells.\nMana Cost: 50 | Cooldown: 1 turn\n\nSkill 2: Overclock\nEffect: Enhances your next shot - fires twice in the same turn. (Dmg: 150-250 per shot)\nMana Cost: 120 | Cooldown: 3 turns\n\nSkill 3: System Overload\nEffect: Deploys a virus - disables one random enemy skill for 3 turns. (Dmg: 250-400)\nMana Cost: 400 | Cooldown: 5 turns\n\nPassive: Firewall\nEffect: Every 4 turns, creates a decoy signal - next enemy shot misses.";

        String seleneLore = "Born during a lunar eclipse, Selene has always been able to see fragments of the future. The Moon Goddess herself appeared to her in a dream, granting her the power to read the stars and predict her enemies' movements. Now she serves as the navy's secret weapon, though her cryptic warnings often confuse more than they help.";
        String seleneSkills = "HP: 1850 | Mana: 500\n\nSkill 1: Lunar Vision\nEffect: Peers into the future - reveals if the enemy's NEXT shot will hit or miss you.\nMana Cost: 60 | Cooldown: 2 turns\n\nSkill 2: Eclipse Binding\nEffect: Calls upon lunar gravity - traps enemy ships in place, cannot move next turn.\nMana Cost: 150 | Cooldown: 3 turns\n\nSkill 3: Crescent Blade\nEffect: Summons a blade of moonlight - cuts diagonally, hitting up to 4 cells. (Dmg: 200-350 per hit)\nMana Cost: 400 | Cooldown: 4 turns\n\nPassive: Moon's Blessing\nEffect: During night time (every 3 turns), your attacks have 20% chance to deal double damage.";

        String skyeLore = "Skye runs the largest cat rescue shelter in the Land of Dawn. With over 200 cats, her life is chaos wrapped in fur. When the ocean rose up threatening to flood her shelter, her cats didn't run - they fought back. Now they follow her into battle, proving that cats really are plotting to take over the world... starting with the sea.";
        String skyeSkills = "HP: 2050 | Mana: 440\n\nSkill 1: Cat Swarm\nEffect: Summons a horde of cats to confuse the enemy - randomizes 3 of their ship positions.\nMana Cost: 70 | Cooldown: 3 turns\n\nSkill 2: Laser Pointer Distraction\nEffect: Dangles a laser pointer - they waste their turn chasing it instead of shooting.\nMana Cost: 50 | Cooldown: 2 turns\n\nSkill 3: Catnip Explosion\nEffect: Blasts enemy with catnip - ships become distracted and deal 50% less damage next turn. (Dmg: 200-350)\nMana Cost: 380 | Cooldown: 4 turns\n\nPassive: Nine Lives\nEffect: First 3 hits that would sink a ship instead leave it at 1 HP.";

        String aerisLore = "Aeris was born into hardship, raised in a poor family where survival required discipline and sacrifice. Working long hours while attending class, Aeris learned to adapt quickly and manage multiple responsibilities at once. Every struggle strengthened his resolve. Instead of being defeated by pressure, he uses it to grow stronger, turning setbacks into power.";
        String aerisSkills = "HP: 2600 | Mana: 600\n\nSkill 1: Adaptive Instinct\nEffect: Reduces incoming damage by 30% for 3 turns. If hit twice in a row, next attack gains +150 damage.\nMana Cost: 120 | Cooldown: 2 turns\n\nSkill 2: Multitask Overdrive\nEffect: Perform two actions in one turn (attack + defend or reveal + attack). Gains +20 speed.\nMana Cost: 180 | Cooldown: 3 turns\n\nSkill 3: Relentless Ascent\nEffect: Damage increases based on missing HP. Below 40% HP, immune to stun for 2 turns. (Dmg: 500-800)\nMana Cost: 250 | Cooldown: 4 turns";

        String flueLore = "Flue is a systems architect obsessed with optimization. He didn't just design the naval command network—he coded its foundational logic. This dedication to efficiency isn't limited to software; he applies the same rigorous structure to his physical life. Recognizing that a flawless simulation requires flawless hardware, he built this untraceable avatar with unparalleled resilience.";
        String flueSkills = "HP: 2200 | Mana: 350\n\nSkill 1: Corruption.EXE\nEffect: Deals damage and silences (prevents skill use) the target for 2 turns. (Dmg: 150-200)\nMana Cost: 45 | Cooldown: 2 turns\n\nSkill 2: Optimized.Fortification.GRID\nEffect: Shields a 2x2 area. Shots landing in this area have a 50% chance to deal 0 damage for 1 turn.\nMana Cost: 80 | Cooldown: 3 turns\n\nSkill 3: Kernel.Decimation.REQ\nEffect: Massive damage to a target cell. Applies permanent debuff reducing enemy skill damage & accuracy by 10%.\nMana Cost: 300 | Cooldown: 5 turns\n\nPassive: Lone.Resolve.CFG\nEffect: Gains 15% permanent damage reduction if no allied shields are active.";

        roster.add(new CharacterData(new Jiji(), "jiji", "JIJI — THE LAZY TECHNOMANCER", jijiLore, jijiSkills));
        roster.add(new CharacterData(new Kael(), "kael", "KAEL — THE SHADOW NAVIGATOR", kaelLore, kaelSkills));
        roster.add(new CharacterData(new Valerius(), "valerius", "VALERIUS — THE IRON SHORELINE", valeriusLore, valeriusSkills));
        roster.add(new CharacterData(new Skye(), "skye", "SKYE — THE CRAZY CAT LADY", skyeLore, skyeSkills));
        roster.add(new CharacterData(new Morgana(), "morgana", "MORGANA — THE SIREN", morganaLore, morganaSkills));
        roster.add(new CharacterData(new Aeris(), "aeris", "AERIS — ADAPTIVE STRATEGIST", aerisLore, aerisSkills));
        roster.add(new CharacterData(new Selene(), "selene", "SELENE — THE MOON ORACLE", seleneLore, seleneSkills));
        roster.add(new CharacterData(new Flue(), "flue", "FLUE — THE SYSTEM BASTION", flueLore, flueSkills));
    }

    private void loadCustomFont() {
        try {
            File fontFile = new File("assets/pixel_font.ttf");
            if (fontFile.exists()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
            } else {
                customFont = new Font("Monospaced", Font.BOLD, 16);
            }
        } catch (Exception e) {
            customFont = new Font("Monospaced", Font.BOLD, 16);
        }
    }

    private void loadImages() {
        try {
            File bgFile = new File("assets/selection.png");
            if (bgFile.exists()) selectionBg = ImageIO.read(bgFile);
            
            File backFile = new File("assets/back.png");
            if (backFile.exists()) backBtnImg = ImageIO.read(backFile);
            
            File deployFile = new File("assets/deploy.png");
            if (deployFile.exists()) deployBtnImg = ImageIO.read(deployFile);

            File selectFile = new File("assets/select.png");
            if (selectFile.exists()) selectTitleImg = ImageIO.read(selectFile);
            
        } catch (Exception ignored) {}
    }

    private void select(int index) {
        if (index >= -1 && index < roster.size()) {
            selectedIndex = index;
            showModal = false; 
            showingSkills = false; 
            hoverOnInfoBtn = false;
            repaint();
        }
    }

    private Rectangle[] getCarouselRects(int imgX, int imgY, int imgW, int imgH) {
        Rectangle[] rects = new Rectangle[roster.size()];
        int mainBoxX = imgX + (int) (0.093 * imgW); 
        int mainBoxY = imgY + (int) (0.224 * imgH); 
        int mainBoxW = (int) (0.855 * imgW); 
        int mainBoxH = (int) (0.442 * imgH); 
        int currentX = mainBoxX;
        int splitW = mainBoxW / roster.size();
        
        for (int i = 0; i < roster.size(); i++) {
            int width = (i == roster.size() - 1) ? (mainBoxX + mainBoxW - currentX) : splitW;
            rects[i] = new Rectangle(currentX, mainBoxY, width, mainBoxH);
            currentX += width;
        }
        return rects;
    }

    private Rectangle getExpandedRect(Rectangle[] baseRects, int imgX, int imgY, int imgW, int imgH) {
        if (selectedIndex == -1) return null;
        int mainBoxX = imgX + (int) (0.093 * imgW); 
        int mainBoxY = imgY + (int) (0.224 * imgH); 
        int mainBoxW = (int) (0.855 * imgW); 
        int mainBoxH = (int) (0.442 * imgH); 

        Rectangle baseRect = baseRects[selectedIndex];
        int expW = (int) (mainBoxW * 0.40); 
        int expX = baseRect.x + (baseRect.width / 2) - (expW / 2);
        
        if (expX < mainBoxX) expX = mainBoxX;
        if (expX + expW > mainBoxX + mainBoxW) expX = mainBoxX + mainBoxW - expW;

        return new Rectangle(expX, mainBoxY, expW, mainBoxH);
    }

    private Rectangle getInfoButtonRect(Rectangle expRect) {
        if (expRect == null) return null;
        int btnSize = 30;
        int padding = 15;
        return new Rectangle(expRect.x + expRect.width - btnSize - padding, expRect.y + padding, btnSize, btnSize);
    }

    private Rectangle getModalRect(int imgX, int imgY, int imgW, int imgH) {
        int modalW = (int) (imgW * 0.65); 
        int modalH = (int) (imgH * 0.55); 
        int modalX = imgX + (imgW - modalW) / 2;
        int modalY = imgY + (imgH - modalH) / 2;
        return new Rectangle(modalX, modalY, modalW, modalH);
    }

    private Rectangle getPageButtonRect(Rectangle modalRect) {
        int btnW = 110;
        int btnH = 35;
        int padding = 20;
        return new Rectangle(modalRect.x + modalRect.width - btnW - padding, modalRect.y + modalRect.height - btnH - padding, btnW, btnH);
    }

    private void handleClick(Point p) {
        if (selectionBg == null) return;
        Rectangle bgRect = getScaledBackgroundRect(getSize(), selectionBg);
        if (bgRect == null) return;

        Rectangle[] carousel = getCarouselRects(bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        Rectangle expRect = getExpandedRect(carousel, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        
        if (showModal) {
            Rectangle modalRect = getModalRect(bgRect.x, bgRect.y, bgRect.width, bgRect.height);
            Rectangle pageBtn = getPageButtonRect(modalRect);
            if (pageBtn.contains(p)) {
                showingSkills = !showingSkills; 
                repaint();
                return;
            }
        }

        Rectangle infoBtn = getInfoButtonRect(expRect);
        if (infoBtn != null && infoBtn.contains(p)) {
            return; 
        }

        if (expRect != null && expRect.contains(p)) {
            select(-1); 
            return;
        }

        for (int i = 0; i < carousel.length; i++) {
            if (carousel[i].contains(p)) {
                select(i);
                return;
            }
        }

        Rectangle backBtn = getButtonRect(BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        if (backBtn.contains(p) && listener != null) {
            listener.onBackToMenu();
            return;
        }

        Rectangle deployBtn = getButtonRect(DEPLOY_BTN_X, DEPLOY_BTN_Y, DEPLOY_BTN_W, DEPLOY_BTN_H, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        if (deployBtn.contains(p) && listener != null && selectedIndex != -1) {
            listener.onCharacterSelected(roster.get(selectedIndex).character);
        }
    }

    private void updateHover(Point p) {
        if (selectionBg == null) return;
        Rectangle bgRect = getScaledBackgroundRect(getSize(), selectionBg);
        if (bgRect == null) return;

        int newHoverSlot = -1;

        Rectangle[] carousel = getCarouselRects(bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        Rectangle expRect = getExpandedRect(carousel, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        Rectangle infoBtn = getInfoButtonRect(expRect);
        Rectangle modalRect = getModalRect(bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        
        boolean isHoveringInfo = infoBtn != null && infoBtn.contains(p);
        boolean isHoveringModal = showModal && modalRect.contains(p);

        if (isHoveringInfo || isHoveringModal) {
            showModal = true;
            newHoverSlot = selectedIndex; 
            hoverOnInfoBtn = isHoveringInfo; 
            
            Rectangle pageBtn = getPageButtonRect(modalRect);
            hoverOnPageBtn = pageBtn.contains(p);
        } else {
            showModal = false;
            showingSkills = false; 
            hoverOnPageBtn = false;
            hoverOnInfoBtn = false;
            
            if (expRect != null && expRect.contains(p)) {
                newHoverSlot = selectedIndex;
            } else {
                for (int i = 0; i < carousel.length; i++) {
                    if (carousel[i].contains(p)) {
                        newHoverSlot = i;
                        break;
                    }
                }
            }
        }
        hoverSlot = newHoverSlot;

        Rectangle backBtn = getButtonRect(BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        hoverOnBack = backBtn.contains(p);

        Rectangle deployBtn = getButtonRect(DEPLOY_BTN_X, DEPLOY_BTN_Y, DEPLOY_BTN_W, DEPLOY_BTN_H, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
        hoverOnDeploy = deployBtn.contains(p);
    }

    private Rectangle getScaledBackgroundRect(Dimension panelSize, BufferedImage img) {
        if (img == null) return null;
        int pw = panelSize.width;
        int ph = panelSize.height;
        double imgAspect = (double) img.getWidth() / img.getHeight();
        double panelAspect = (double) pw / ph;

        int drawW, drawH;
        if (panelAspect > imgAspect) {
            drawH = ph;
            drawW = (int) (ph * imgAspect);
        } else {
            drawW = pw;
            drawH = (int) (pw / imgAspect);
        }
        int drawX = (pw - drawW) / 2;
        int drawY = (ph - drawH) / 2 + 10;
        return new Rectangle(drawX, drawY, drawW, drawH);
    }

    private Rectangle getButtonRect(double xRel, double yRel, double wRel, double hRel, int imgX, int imgY, int imgW, int imgH) {
        int x = imgX + (int) (xRel * imgW);
        int y = imgY + (int) (yRel * imgH);
        int w = (int) (wRel * imgW);
        int h = (int) (hRel * imgH);
        return new Rectangle(x, y, w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // Draw ocean background (same as MainMenuPanel)
        try {
            ImageIcon bg = new ImageIcon("assets/naval.jpg");
            g2.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), null);
        } catch (Exception ex) {
            g2.setColor(new Color(25, 25, 112));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw animated particles
        for (CannonFlash f : flashList) f.draw(g2);
        for (SmokeParticle s : smokeList) s.draw(g2);
        for (SplashParticle p : splashList) p.draw(g2);

        if (selectionBg != null) {
            Rectangle bgRect = getScaledBackgroundRect(getSize(), selectionBg);
            if (bgRect != null) {
                g2.drawImage(selectionBg, bgRect.x, bgRect.y, bgRect.width, bgRect.height, null);
                
                drawTopTitle(g2, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
                drawCarousel(g2, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
                drawCharacterName(g2, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
                drawButtons(g2, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
                
                if (showModal && selectedIndex != -1) {
                    drawHoverModal(g2, bgRect.x, bgRect.y, bgRect.width, bgRect.height);
                }
            }
        }
        g2.dispose();
    }
    
    private void spawnParticles() {
        if (random.nextInt(3) == 0) {
            int[] pos = CANNON_POSITIONS[random.nextInt(CANNON_POSITIONS.length)];
            smokeList.add(new SmokeParticle(
                pos[0] + random.nextInt(30) - 15,
                pos[1] + random.nextInt(10),
                random.nextInt(8) + 6
            ));
        }
        
        if (random.nextInt(60) == 0) {
            int[] pos = CANNON_POSITIONS[random.nextInt(CANNON_POSITIONS.length)];
            flashList.add(new CannonFlash(pos[0], pos[1]));
            
            for (int i = 0; i < 8; i++) {
                smokeList.add(new SmokeParticle(
                    pos[0] + random.nextInt(40) - 20,
                    pos[1] + random.nextInt(10),
                    random.nextInt(12) + 8
                ));
            }
        }
        
        if (random.nextInt(40) == 0) {
            int[] pos = SPLASH_POSITIONS[random.nextInt(SPLASH_POSITIONS.length)];
            int cx = pos[0] + random.nextInt(60) - 30;
            int cy = pos[1] + random.nextInt(20) - 10;
            
            for (int i = 0; i < 12; i++) {
                splashList.add(new SplashParticle(cx, cy));
            }
        }
    }
    
    private void updateParticles() {
        Iterator<SmokeParticle> si = smokeList.iterator();
        while (si.hasNext()) {
            if (si.next().update())
                si.remove();
        }
        
        Iterator<SplashParticle> sp = splashList.iterator();
        while (sp.hasNext()) {
            if (sp.next().update())
                sp.remove();
        }
        
        Iterator<CannonFlash> cf = flashList.iterator();
        while (cf.hasNext()) {
            if (cf.next().update())
                cf.remove();
        }
    }

    private void drawTopTitle(Graphics2D g2, int imgX, int imgY, int imgW, int imgH) {
        if (selectTitleImg == null) return;
        
        int titleW = (int) (0.330 * imgW); 
        int titleH = (int) (0.090 * imgH); 
        int titleX = imgX + (imgW - titleW) / 2 + 10;  
        int titleY = imgY + (int) (0.075 * imgH) + 5; 
        
        g2.drawImage(selectTitleImg, titleX, titleY, titleW, titleH, null);
    }

    private void drawCharacterName(Graphics2D g2, int imgX, int imgY, int imgW, int imgH) {
        if (selectedIndex == -1) return;
        
        int boxX = imgX + (int) (0.150 * imgW); 
        int boxY = imgY + (int) (0.725 * imgH); 
        int boxW = (int) (0.700 * imgW);        
        int boxH = (int) (0.080 * imgH);        
        
        String name = roster.get(selectedIndex).getDisplayName();
        
        float fontSize = 28f;
        g2.setFont(customFont.deriveFont(Font.PLAIN, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        while (fm.stringWidth(name) > boxW - 20 && fontSize > 14f) {
            fontSize -= 1f;
            g2.setFont(customFont.deriveFont(Font.PLAIN, fontSize));
            fm = g2.getFontMetrics();
        }
        
        int textWidth = fm.stringWidth(name);
        int textX = boxX + (boxW - textWidth) / 2 + 35;
        int textY = boxY + ((boxH - fm.getHeight()) / 2) + fm.getAscent();
        
        g2.setColor(Color.BLACK);
        g2.drawString(name, textX + 2, textY + 2);
        g2.drawString(name, textX + 3, textY + 3);
        
        g2.setColor(new Color(240, 245, 250));
        g2.drawString(name, textX, textY);
    }

    private void drawCarousel(Graphics2D g2, int imgX, int imgY, int imgW, int imgH) {
        Rectangle[] rects = getCarouselRects(imgX, imgY, imgW, imgH);
        Shape originalClip = g2.getClip();
        int cornerRadius = 15; 

        for (int i = 0; i < roster.size(); i++) {
            Rectangle r = rects[i];
            String key = roster.get(i).imageKey;
            BufferedImage bgImg = imageCache.getGrayBg(key);

            Shape roundClip = new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, cornerRadius, cornerRadius);
            g2.setClip(roundClip);

            if (bgImg != null) {
                double scaleX = (double) r.width / bgImg.getWidth();
                double scaleY = (double) r.height / bgImg.getHeight();
                double scale = Math.max(scaleX, scaleY);
                int drawW = (int) (bgImg.getWidth() * scale);
                int drawH = (int) (bgImg.getHeight() * scale);
                int drawX = r.x + (r.width - drawW) / 2;
                int drawY = r.y + (r.height - drawH) / 2;
                g2.drawImage(bgImg, drawX, drawY, drawW, drawH, null);
            }

            int alpha = (selectedIndex == -1) ? 40 : 150; 
            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fillRoundRect(r.x, r.y, r.width, r.height, cornerRadius, cornerRadius);

            if (i == hoverSlot && selectedIndex == -1) {
                g2.setColor(SLOT_HOVER);
                g2.fillRoundRect(r.x, r.y, r.width, r.height, cornerRadius, cornerRadius); 
            }

            g2.setClip(originalClip);
            g2.setColor(new Color(40, 40, 40));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(r.x, r.y, r.width, r.height, cornerRadius, cornerRadius); 
        }

        if (selectedIndex != -1) {
            Rectangle expRect = getExpandedRect(rects, imgX, imgY, imgW, imgH);
            String key = roster.get(selectedIndex).imageKey;
            BufferedImage bgImg = imageCache.getBg(key);
            
            Shape roundClip = new RoundRectangle2D.Float(expRect.x, expRect.y, expRect.width, expRect.height, cornerRadius, cornerRadius);
            g2.setClip(roundClip);

            if (bgImg != null) {
                double scaleX = (double) expRect.width / bgImg.getWidth();
                double scaleY = (double) expRect.height / bgImg.getHeight();
                double scale = Math.max(scaleX, scaleY);
                int drawW = (int) (bgImg.getWidth() * scale);
                int drawH = (int) (bgImg.getHeight() * scale);
                int drawX = expRect.x + (expRect.width - drawW) / 2;
                int drawY = expRect.y + (expRect.height - drawH) / 2;
                g2.drawImage(bgImg, drawX, drawY, drawW, drawH, null);
            }
            
            g2.setClip(originalClip);
            
            Rectangle infoBtn = getInfoButtonRect(expRect);
            if (infoBtn != null) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(infoBtn.x, infoBtn.y, infoBtn.width, infoBtn.height, 10, 10);
                
                g2.setColor(showModal ? TEXT_GOLD : Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(infoBtn.x, infoBtn.y, infoBtn.width, infoBtn.height, 10, 10);
                
                g2.setFont(customFont.deriveFont(Font.PLAIN, 20f));
                FontMetrics fm = g2.getFontMetrics();
                int qX = infoBtn.x + (infoBtn.width - fm.stringWidth("?")) / 2;
                int qY = infoBtn.y + ((infoBtn.height - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("?", qX, qY);
            }
        }
    }

    private void drawHoverModal(Graphics2D g2, int imgX, int imgY, int imgW, int imgH) {
        Rectangle modal = getModalRect(imgX, imgY, imgW, imgH);
        
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(modal.x + 8, modal.y + 8, modal.width, modal.height);
        g2.setColor(new Color(42, 47, 56, 245));
        g2.fillRect(modal.x, modal.y, modal.width, modal.height);
        g2.setColor(new Color(75, 82, 95));
        g2.setStroke(new BasicStroke(6f));
        g2.drawRect(modal.x, modal.y, modal.width, modal.height);
        g2.setColor(new Color(15, 18, 22));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(modal.x + 3, modal.y + 3, modal.width - 6, modal.height - 6);
        
        g2.setColor(new Color(20, 25, 30));
        g2.fillRect(modal.x + 8, modal.y + 8, 6, 6);
        g2.fillRect(modal.x + modal.width - 14, modal.y + 8, 6, 6);
        g2.fillRect(modal.x + 8, modal.y + modal.height - 14, 6, 6);
        g2.fillRect(modal.x + modal.width - 14, modal.y + modal.height - 14, 6, 6);
        g2.setColor(new Color(120, 130, 145)); 
        g2.fillRect(modal.x + 9, modal.y + 9, 2, 2);
        g2.fillRect(modal.x + modal.width - 13, modal.y + 9, 2, 2);
        g2.fillRect(modal.x + 9, modal.y + modal.height - 13, 2, 2);
        g2.fillRect(modal.x + modal.width - 13, modal.y + modal.height - 13, 2, 2);

        int padding = 35; 
        int currentY = modal.y + 45; 
        
        g2.setFont(customFont.deriveFont(Font.PLAIN, 22f));
        String title = showingSkills ? "STATS & SKILLS" : "CHARACTER LORE";
        
        g2.setColor(Color.BLACK);
        g2.drawString(title, modal.x + padding + 2, currentY + 2);
        g2.setColor(new Color(240, 245, 250)); 
        g2.drawString(title, modal.x + padding, currentY);
        
        g2.setColor(new Color(25, 30, 35)); 
        g2.fillRect(modal.x + padding, currentY + 10, modal.width - (padding * 2), 4);
        g2.setColor(new Color(75, 82, 95)); 
        g2.fillRect(modal.x + padding, currentY + 14, modal.width - (padding * 2), 2);

        CharacterData cd = roster.get(selectedIndex);
        String textToDraw = showingSkills ? cd.getSkills() : cd.getLoreDescription();
        
        g2.setFont(customFont.deriveFont(Font.PLAIN, showingSkills ? 13f : 15f));
        FontMetrics fm = g2.getFontMetrics();
        int maxTextWidth = modal.width - (padding * 2);
        currentY += 40; 
        
        String[] lines = textToDraw.split("\n");

        for (String lineText : lines) {
            if (lineText.trim().isEmpty()) {
                currentY += fm.getHeight(); 
                continue;
            }
            
            String[] words = lineText.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (fm.stringWidth(currentLine + word) < maxTextWidth) {
                    currentLine.append(word).append(" ");
                } else {
                    g2.setColor(Color.BLACK);
                    g2.drawString(currentLine.toString(), modal.x + padding + 2, currentY + 2);
                    g2.setColor(new Color(230, 235, 240));
                    g2.drawString(currentLine.toString(), modal.x + padding, currentY);
                    
                    currentLine = new StringBuilder(word + " ");
                    currentY += fm.getHeight() + (showingSkills ? 4 : 6); 
                }
            }
            if (currentLine.length() > 0) {
                g2.setColor(Color.BLACK);
                g2.drawString(currentLine.toString(), modal.x + padding + 2, currentY + 2);
                g2.setColor(new Color(230, 235, 240));
                g2.drawString(currentLine.toString(), modal.x + padding, currentY);
                currentY += fm.getHeight() + (showingSkills ? 4 : 6); 
            }
        }
        
        Rectangle pageBtn = getPageButtonRect(modal);
        g2.setColor(hoverOnPageBtn ? new Color(75, 82, 95) : new Color(25, 30, 35));
        g2.fillRect(pageBtn.x, pageBtn.y, pageBtn.width, pageBtn.height);
        
        g2.setColor(hoverOnPageBtn ? TEXT_GOLD : new Color(150, 160, 175));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(pageBtn.x, pageBtn.y, pageBtn.width, pageBtn.height);
        
        String btnText = showingSkills ? "< LORE" : "SKILLS >";
        g2.setFont(customFont.deriveFont(Font.PLAIN, 16f));
        int tX = pageBtn.x + (pageBtn.width - fm.stringWidth(btnText)) / 2;
        int tY = pageBtn.y + ((pageBtn.height - fm.getHeight()) / 2) + fm.getAscent();
        
        g2.setColor(Color.BLACK);
        g2.drawString(btnText, tX + 2, tY + 2);
        g2.setColor(hoverOnPageBtn ? TEXT_GOLD : Color.WHITE);
        g2.drawString(btnText, tX, tY);
    }

    private void drawButtons(Graphics2D g2, int imgX, int imgY, int imgW, int imgH) {
        Rectangle backBtn = getButtonRect(BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H, imgX, imgY, imgW, imgH);
        Rectangle deployBtn = getButtonRect(DEPLOY_BTN_X, DEPLOY_BTN_Y, DEPLOY_BTN_W, DEPLOY_BTN_H, imgX, imgY, imgW, imgH);
        
        drawImageButton(g2, backBtnImg, backBtn, hoverOnBack);
        
        boolean canDeploy = (selectedIndex != -1);
        if (canDeploy) {
            drawImageButton(g2, deployBtnImg, deployBtn, hoverOnDeploy);
        } else {
            if (deployBtnImg != null) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2.drawImage(deployBtnImg, deployBtn.x, deployBtn.y, deployBtn.width, deployBtn.height, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        }
    }

    private void drawImageButton(Graphics2D g2, BufferedImage img, Rectangle rect, boolean isHovered) {
        if (img == null) {
            g2.setColor(Color.RED);
            g2.drawRect(rect.x, rect.y, rect.width, rect.height);
            return;
        }

        if (isHovered) {
            int hoverInflate = 4;
            g2.drawImage(img, rect.x - hoverInflate, rect.y - hoverInflate, 
                         rect.width + (hoverInflate*2), rect.height + (hoverInflate*2), null);
            
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(rect.x - hoverInflate, rect.y - hoverInflate, 
                             rect.width + (hoverInflate*2), rect.height + (hoverInflate*2), 10, 10);
        } else {
            g2.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
        }
    }

    // Particle classes (same as MainMenuPanel)
    class SmokeParticle {
        float x, y, vx, vy, size, alpha;
        SmokeParticle(int x, int y, int size) {
            this.x = x; this.y = y; this.size = size;
            this.vx = random.nextFloat() * 1.5f - 0.75f;
            this.vy = -(random.nextFloat() * 1.5f + 0.5f);
            this.alpha = 0.6f;
        }
        boolean update() {
            x += vx; y += vy; size += 0.3f; alpha -= 0.008f;
            return alpha <= 0;
        }
        void draw(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, alpha)));
            g2.setColor(new Color(60, 60, 60));
            g2.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
    
    class SplashParticle {
        float x, y, vx, vy, alpha;
        SplashParticle(int cx, int cy) {
            this.x = cx; this.y = cy;
            float angle = random.nextFloat() * (float)Math.PI;
            float speed = random.nextFloat() * 6f + 2f;
            this.vx = (float)(Math.cos(angle) * speed);
            this.vy = -(float)(Math.sin(angle) * speed) - 2f;
            this.alpha = 0.9f;
        }
        boolean update() {
            x += vx; y += vy; vy += 0.3f; alpha -= 0.025f;
            return alpha <= 0;
        }
        void draw(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, alpha)));
            g2.setColor(new Color(200, 230, 255));
            g2.fillOval((int)x, (int)y, 4, 4);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
    
    class CannonFlash {
        int x, y; float alpha, size;
        CannonFlash(int x, int y) {
            this.x = x; this.y = y; this.alpha = 1.0f; this.size = 40f;
        }
        boolean update() {
            alpha -= 0.08f; size += 5f;
            return alpha <= 0;
        }
        void draw(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, alpha * 0.5f)));
            g2.setColor(new Color(255, 120, 0));
            g2.fillOval((int)(x - size), (int)(y - size), (int)(size * 2), (int)(size * 2));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, alpha)));
            g2.setColor(new Color(255, 240, 100));
            g2.fillOval((int)(x - size/3), (int)(y - size/3), (int)(size * 0.7f), (int)(size * 0.7f));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private static class CharacterData {
        final GameCharacter character;
        final String imageKey;
        final String displayName;
        final String lore; 
        final String skills;

        CharacterData(GameCharacter ch, String key, String displayName, String lore, String skills) {
            this.character = ch;
            this.imageKey = key;
            this.displayName = displayName;
            this.lore = lore;
            this.skills = skills;
        }
        
        String getName() { return character.getName(); }
        String getDisplayName() { return displayName; }
        String getLoreDescription() { return lore; }
        String getSkills() { return skills; }
    }

    private static class ImageCache {
        private static final String PATH = "assets/";
        private static final String[] EXTS = {".jpg", ".jpeg", ".png", ".gif"};
        private final ArrayList<CharacterData> roster;
        private final java.util.HashMap<String, BufferedImage> colorMap = new java.util.HashMap<>();
        private final java.util.HashMap<String, BufferedImage> grayMap = new java.util.HashMap<>();
        private final java.util.HashMap<String, BufferedImage> bgMap = new java.util.HashMap<>();
        private final java.util.HashMap<String, BufferedImage> grayBgMap = new java.util.HashMap<>();

        ImageCache(ArrayList<CharacterData> roster) { this.roster = roster; }

        void loadAll() {
            for (CharacterData cd : roster) {
                String normalBgKey = cd.imageKey + "_bg";
                String dotBgKey = cd.imageKey + ".bg";
                
                loadInto(cd.imageKey, colorMap);
                
                if (!loadInto(normalBgKey, bgMap)) {
                    loadInto(dotBgKey, bgMap);
                    if (bgMap.containsKey(dotBgKey)) {
                        bgMap.put(normalBgKey, bgMap.get(dotBgKey));
                    }
                }
                
                if (colorMap.containsKey(cd.imageKey)) {
                    grayMap.put(cd.imageKey, toGray(colorMap.get(cd.imageKey)));
                }
                
                if (bgMap.containsKey(normalBgKey)) {
                    grayBgMap.put(normalBgKey, toGray(bgMap.get(normalBgKey)));
                }
            }
        }

        private boolean loadInto(String key, java.util.HashMap<String, BufferedImage> map) {
            for (String ext : EXTS) {
                File f = new File(PATH + key + ext);
                if (!f.exists()) continue;
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) { map.put(key, img); return true; }
                } catch (Exception ignored) {}
            }
            return false;
        }

        BufferedImage getColor(String key) { return colorMap.get(key); }
        BufferedImage getGray(String key)  { return grayMap.get(key); }
        BufferedImage getBg(String key)    { return bgMap.get(key + "_bg"); }
        BufferedImage getGrayBg(String key){ return grayBgMap.get(key + "_bg"); }

        private BufferedImage toGray(BufferedImage src) {
            BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < src.getHeight(); y++) {
                for (int x = 0; x < src.getWidth(); x++) {
                    int argb = src.getRGB(x, y);
                    int a = (argb >> 24) & 0xFF;
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;
                    int lum = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                    out.setRGB(x, y, (a << 24) | (lum << 16) | (lum << 8) | lum);
                }
            }
            return out;
        }
    }

    public interface CharacterSelectListener {
        void onCharacterSelected(GameCharacter character);
        void onBackToMenu();
    }
}