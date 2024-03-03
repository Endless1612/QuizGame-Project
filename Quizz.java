import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.nio.file.*;

class Pair {
    private String key;
    private String value;

    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

class Quiz {
    private String name;
    private String playerName;
    private String category;
    private int score;
    private int highScore;
    private List<Pair> categories;
    private List<Question> questions;
    private List<String> scoreHistory;

    public Quiz(String name) {
        this.name = name;
        this.score = 0;
        this.highScore = 0;
        this.categories = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.scoreHistory = new ArrayList<>();

        categories.add(new Pair("S", "Science"));
        categories.add(new Pair("G", "General"));
        categories.add(new Pair("A", "Animal"));
        categories.add(new Pair("F", "Food"));
        categories.add(new Pair("H", "History"));

        this.questions = loadQuestionsFromCSV();
        this.scoreHistory = new ArrayList<>();
    }

    private List<Question> loadQuestionsFromCSV() {
        List<Question> questions = new ArrayList<>();
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader("Question.csv"))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                List<String> options = Arrays.asList(data[2], data[3], data[4], data[5]);
                Question question = new Question(data[0], data[1], options, data[6]);
                questions.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(questions);
        return questions;
    }

    public void Cut() {
        System.out.println("------------------------------------- \n------------------------------------- ");
    }

    public void startQuiz() {
        while (true) {
            Collections.shuffle(questions);
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your name: ");
            playerName = scanner.nextLine();

            while (playerName.trim().isEmpty()) {
                System.out.println("Invalid input. Please enter a name.");
                System.out.print("Enter your name: ");
                this.playerName = new Scanner(System.in).nextLine();
            }
            Cut();
            displayCategories();
            Cut();
            System.out.print(" : ");
            selectCategory(new Scanner(System.in).nextLine());

            for (Question question : questions) {
                if (question.getCategory().equals(this.category)) {
                    Cut();
                    displayQuestion(question);
                    Cut();
                    System.out.print("Answer : ");
                    String userAnswer = new Scanner(System.in).nextLine();

                    if (userAnswer.equalsIgnoreCase("quit")) {
                        System.out.println("Exiting the quiz...");
                        break;
                    }

                    checkAnswer(userAnswer, question);
                    displayScore();
                }
            }

            saveScore();
            displayMenu();

        }
    }

    public void displayMenu() {
        while (true) {
            Cut();
            System.out.println("Select menu:");
            System.out.println("P: Play again.");
            System.out.println("S: View all scores.");
            System.out.println("Q: Quit the game.");
            Cut();
            System.out.print("Please select an option: ");
            String menuOption = new Scanner(System.in).nextLine();
            if (menuOption.equalsIgnoreCase("P")) {
                score = 0;
                Cut();
                break;
            } else if (menuOption.equalsIgnoreCase("S")) {
                displayAllScores();
                break;
            } else if (menuOption.equalsIgnoreCase("Q")) {
                Cut();
                System.out.println();
                System.out.println("You have chosen to quit the game. Goodbye !!!!");
                System.out.println();
                Cut();
                System.exit(0);
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void displayCategories() {
        System.out.println("Select Categories:");
        for (Pair pair : categories) {
            System.out.println(pair.getKey() + ": " + pair.getValue());
        }

    }

    public void displayHighScore() {
        String line;
        int maxScore = 0;
        String maxScorePlayer = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("scores.csv"))) {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String playerName = data[0].split(":")[1].trim();
                int score = Integer.parseInt(data[2].split(":")[1].trim());
                if (score > maxScore) {
                    maxScore = score;
                    maxScorePlayer = playerName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("High Score: " + maxScore + ", Player: " + maxScorePlayer);
    }

    public void displayAllScores() {
        System.out.println();
        System.out.println("********************** SCORE HISTORY **********************");
        try (BufferedReader reader = new BufferedReader(new FileReader("scores.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("* " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        displayHighScore();
        System.out.println("***********************************************************");
        System.out.println();
        System.out.println("Press enter to play a new quiz.");
        new Scanner(System.in).nextLine();
        score = 0;
    }

    public void selectCategory(String categoryKey) {
        boolean isValidCategory = false;
        for (Pair pair : categories) {
            if (pair.getKey().equals(categoryKey.toUpperCase())) {
                this.category = pair.getValue();
                isValidCategory = true;
                break;
            }
        }
        if (!isValidCategory) {
            System.out.println("Invalid category. Please try again.");
            System.out.print(": ");
            selectCategory(new Scanner(System.in).nextLine());
        }
    }

    public void displayQuestion(Question question) {
        System.out.println(question.getQuestionText());
        List<String> options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ": " + options.get(i));
        }
    }

    public void checkAnswer(String userAnswer, Question question) {

        if (userAnswer.equalsIgnoreCase("quit")) {
            System.out.println("Exiting the quiz...");
            saveScore();
            return;
        }

        try {
            int answerIndex = Integer.parseInt(userAnswer) - 1;
            if (answerIndex >= 0 && answerIndex < question.getOptions().size()) {
                if (userAnswer.equals(question.getCorrectAnswer())) {
                    System.out.println("You're correct ✅✅✅");
                    score++;
                } else {
                    System.out.println("You're wrong ❌❌❌ ");
                }
            } else {
                Cut();
                System.out.println("Invalid answer. Please try again.");
                Cut();
                System.out.print("Please input 1-4 : ");
                checkAnswer(new Scanner(System.in).nextLine(), question);
            }

        } catch (NumberFormatException e) {
            Cut();
            System.out.println("Invalid input. Please enter a number.");
            Cut();
            System.out.print("Please input number : ");
            checkAnswer(new Scanner(System.in).nextLine(), question);
        }
    }

    public void displayScore() {
        Cut();

        System.out.println(playerName + ", your score: " + score);

    }

    public void saveScore() {
        String newScore = String.format("[Name: %s, Category: %s, Score: %d, Date: %s]", playerName, category, score,
                LocalDate.now());
        try (FileWriter writer = new FileWriter("scores.csv", true)) {
            writer.write(newScore + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Question {
    private String category;
    private String questionText;
    private List<String> options;
    private String correctAnswer;

    public Question(String category, String questionText, List<String> options, String correctAnswer) {
        this.category = category;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

class Main {
    public static void main(String[] args) {
        Quiz quiz = new Quiz("Athena Quiz");

        quiz.startQuiz();

    }
}
