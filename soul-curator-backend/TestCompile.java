import java.time.LocalDateTime;

public class TestCompile {
    public static void main(String[] args) {
        System.out.println("Testing compilation...");
        
        // 测试类型转换
        int completedQuestions = 2;
        int totalQuestions = 5;
        
        if (totalQuestions > 0) {
            double percentage = (double) completedQuestions / totalQuestions * 100;
            long completionPercentage = Math.min(100, Math.round(percentage));
            System.out.println("Percentage: " + percentage);
            System.out.println("Rounded: " + Math.round(percentage));
            System.out.println("Completion: " + completionPercentage);
        }
        
        System.out.println("Test passed!");
    }
}