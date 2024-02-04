import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
public class Game {
    private class Cards{
        String value;
        String type;
        Cards(String value, String type){
            this.value = value;
            this.type = type;
        }

        public String toString(){
            return value+"-"+type;
        }

        public int getValue(){
            if ("AJQK".contains(value)){
                if(value == "A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce(){
            return value =="A";
        }

        public String getImagePath() {
            return "/cards/" + toString() + ".png";
        }
        
    }

    ArrayList<Cards> deck;
    Random r = new Random();
    Cards hiddenCard;
    ArrayList<Cards> dealerHand;
    int dealerSum;
    int dealerAceCount;
    ArrayList<Cards> playerHand;
    int playerSum;
    int playerAceCount;
    int boardwidth = 500;
    int boardHeight = 550;
    int cardWidth = 100;
    int cardHeight = 150;

    String username;
    JFrame frame = new JFrame("Black Jack");
    JPanel gamepanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!staybutton.isEnabled()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                for(int i=0; i< dealerHand.size(); i++){
                    Cards card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth+25+(cardWidth+5)*i, 20, cardWidth, cardHeight, null);
                }
                for(int i=0; i< playerHand.size(); i++){
                    Cards card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20+ (cardWidth+5)*i, 320, cardWidth, cardHeight, null);
                }
                if(!staybutton.isEnabled()){
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("STAY: ");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if(playerSum > 21){
                        message = "You Lose!";
                    }else if(dealerSum >21){
                        message = "You Win!";
                    }else if(playerSum == dealerSum){
                        message = "Tie!";
                    }else if(playerSum>dealerSum){
                        message = "You Win!";
                    }else if(playerSum < dealerSum){
                        message = "You Lose!";
                    }

                    g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton staybutton = new JButton("Stay");
    JButton hitButton = new JButton("Hit");
    JLabel usernameLabel = new JLabel();

    Game(){
        getUsername();
        startGame();
        setupUI();
        frame.setVisible(true);
        frame.setSize(boardwidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamepanel.setLayout(new BorderLayout());
        gamepanel.setBackground(new Color(53, 101, 77));
        frame.add(gamepanel);

        hitButton.setFocusable(true);
        buttonPanel.add(hitButton);
        staybutton.setFocusable(true);
        buttonPanel.add(staybutton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cards card = deck.remove(deck.size()-1);
                playerSum +=card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if(reducePlayerAce()>21){
                    hitButton.setEnabled(false);
                }
                gamepanel.repaint();
            }
        });
        staybutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                staybutton.setEnabled(false);
                while(dealerSum<17){
                    Cards card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamepanel.repaint();
            }
        });
        gamepanel.repaint();
    }

    private void setupUI(){
        frame.setLayout(new BorderLayout());
        frame.add(gamepanel, BorderLayout.CENTER);
        buttonPanel.add(hitButton);
        buttonPanel.add(staybutton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(usernameLabel, BorderLayout.SOUTH);
        usernameLabel.setText("Player: "+username);
        usernameLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        frame.setVisible(true);
        frame.setSize(boardwidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void getUsername(){
        username = JOptionPane.showInputDialog(frame, "Enter a username below: ");
        if(username == null || username.trim().isEmpty()){
            username = "User";
        }
    }
    public void startGame() {
       JOptionPane.showMessageDialog(frame, "Welcome To The Game "+username+"!\n Please type ENTER to star the game!\n Good Luck!");
        buildDeck();
        shuffleDeck();
        dealerHand = new ArrayList<Cards>();
        dealerSum = 0;
        dealerAceCount = 0;
        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Cards card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("DEALER: ");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        playerHand = new ArrayList<Cards>();
        playerSum = 0;
        playerAceCount = 0;
        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
        System.out.println("PLAYER: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);
    }
    public void buildDeck() {
        deck = new ArrayList<Cards>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Cards card = new Cards(values[j], types[i]);
                deck.add(card);
            }
        }
        System.out.println("BUILD DECK: ");
        System.out.println(deck);
    }
        public void shuffleDeck(){
            for(int i=0; i< deck.size(); i++){
                int j=r.nextInt(deck.size());
                Cards currentCard = deck.get(i);
                Cards randomCard = deck.get(j);
                deck.set(i, randomCard);
                deck.set(j, currentCard);
            }
            System.out.println("AFTER SHUFFLE: ");
            System.out.println(deck);

        }
        public int reducePlayerAce(){
            while(playerSum>21 && playerAceCount>0){
                playerSum -= 10;
                playerAceCount -= 1;
            }
            return playerSum;
        }

        public int reduceDealerAce(){
            while(dealerSum >21 && dealerAceCount>0){
                dealerSum -= 10;
                dealerAceCount -= 1;
            }
            return dealerSum;
        }

}
