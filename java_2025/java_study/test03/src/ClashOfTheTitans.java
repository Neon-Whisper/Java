public class ClashOfTheTitans {

    public static void main(String[] args) {

        Zues z = new Zues();
        Titan t = new Titan();
        do{
            z.attack(t);
            t.attack(z);
        } while (z.getHP() > 0 && t.getHP() > 0);

        if (z.getHP() > 0) {
            System.out.println("泰坦HP为" + t.getHP() + "，已经失败，胜利者是宙斯！");
        } else {
            System.out.println("宙斯HP为" + z.getHP() + "，已经失败，胜利者是泰坦！");
        }
    }
}
