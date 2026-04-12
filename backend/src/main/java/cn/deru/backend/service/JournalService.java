package cn.deru.backend.service;

import cn.deru.backend.model.Journal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JournalService {
    private List<Journal> journals = new ArrayList<>();

    public JournalService() {
        // 初始化期刊数据
        initializeJournals();
    }

    private void initializeJournals() {
        // 自然科学
        Journal journal1 = new Journal();
        journal1.setId(1L);
        journal1.setTitle("Nature");
        journal1.setAuthor("Nature Publishing Group");
        journal1.setCategory("自然科学");
        journal1.setCover("https://via.placeholder.com/200x280/10b981/ffffff?text=Nature");
        journal1.setDescription("Nature是世界上最权威的科学期刊之一，涵盖生物学、物理学、化学等多个学科领域的原创研究。");
        journal1.setPublisher("Nature Publishing Group");
        journal1.setIssn("0028-0836");

        Journal journal2 = new Journal();
        journal2.setId(2L);
        journal2.setTitle("Science");
        journal2.setAuthor("American Association for the Advancement of Science");
        journal2.setCategory("自然科学");
        journal2.setCover("https://via.placeholder.com/200x280/3b82f6/ffffff?text=Science");
        journal2.setDescription("Science是一本综合性科学期刊，发表各领域的重要研究成果和评论。");
        journal2.setPublisher("AAAS");
        journal2.setIssn("0036-8075");

        // 医学
        Journal journal3 = new Journal();
        journal3.setId(3L);
        journal3.setTitle("The New England Journal of Medicine");
        journal3.setAuthor("Massachusetts Medical Society");
        journal3.setCategory("医学");
        journal3.setCover("https://via.placeholder.com/200x280/ef4444/ffffff?text=NEJM");
        journal3.setDescription("NEJM是世界上最古老、最受尊敬的医学期刊之一，发表医学领域的原创研究和临床实践。");
        journal3.setPublisher("Massachusetts Medical Society");
        journal3.setIssn("0028-4793");

        Journal journal4 = new Journal();
        journal4.setId(4L);
        journal4.setTitle("The Lancet");
        journal4.setAuthor("Elsevier");
        journal4.setCategory("医学");
        journal4.setCover("https://via.placeholder.com/200x280/f97316/ffffff?text=Lancet");
        journal4.setDescription("The Lancet是全球最权威的医学期刊之一，发表医学研究、评论和观点。");
        journal4.setPublisher("Elsevier");
        journal4.setIssn("0140-6736");

        // 计算机
        Journal journal5 = new Journal();
        journal5.setId(5L);
        journal5.setTitle("Communications of the ACM");
        journal5.setAuthor("Association for Computing Machinery");
        journal5.setCategory("计算机");
        journal5.setCover("https://via.placeholder.com/200x280/8b5cf6/ffffff?text=ACM");
        journal5.setDescription("CACM是计算机科学领域的权威期刊，发表计算机理论、实践和应用方面的研究。");
        journal5.setPublisher("Association for Computing Machinery");
        journal5.setIssn("0001-0782");

        Journal journal6 = new Journal();
        journal6.setId(6L);
        journal6.setTitle("IEEE Transactions on Computers");
        journal6.setAuthor("IEEE Computer Society");
        journal6.setCategory("计算机");
        journal6.setCover("https://via.placeholder.com/200x280/14b8a6/ffffff?text=IEEE");
        journal6.setDescription("IEEE Transactions on Computers是计算机工程领域的顶级期刊，发表计算机硬件和软件方面的研究。");
        journal6.setPublisher("IEEE Computer Society");
        journal6.setIssn("0018-9340");

        // 物理
        Journal journal7 = new Journal();
        journal7.setId(7L);
        journal7.setTitle("Physical Review Letters");
        journal7.setAuthor("American Physical Society");
        journal7.setCategory("物理");
        journal7.setCover("https://via.placeholder.com/200x280/3b82f6/ffffff?text=PRL");
        journal7.setDescription("Physical Review Letters是物理学领域的顶级期刊，发表物理学各分支的重要研究成果。");
        journal7.setPublisher("American Physical Society");
        journal7.setIssn("0031-9007");

        Journal journal8 = new Journal();
        journal8.setId(8L);
        journal8.setTitle("Nature Physics");
        journal8.setAuthor("Nature Publishing Group");
        journal8.setCategory("物理");
        journal8.setCover("https://via.placeholder.com/200x280/8b5cf6/ffffff?text=Nat+Phys");
        journal8.setDescription("Nature Physics是物理学领域的权威期刊，发表物理学前沿研究成果。");
        journal8.setPublisher("Nature Publishing Group");
        journal8.setIssn("1745-2473");

        // 化学
        Journal journal9 = new Journal();
        journal9.setId(9L);
        journal9.setTitle("Journal of the American Chemical Society");
        journal9.setAuthor("American Chemical Society");
        journal9.setCategory("化学");
        journal9.setCover("https://via.placeholder.com/200x280/ec4899/ffffff?text=JACS");
        journal9.setDescription("JACS是化学领域的顶级期刊，发表化学各分支的原创研究。");
        journal9.setPublisher("American Chemical Society");
        journal9.setIssn("0002-7863");

        Journal journal10 = new Journal();
        journal10.setId(10L);
        journal10.setTitle("Angewandte Chemie");
        journal10.setAuthor("Wiley-VCH");
        journal10.setCategory("化学");
        journal10.setCover("https://via.placeholder.com/200x280/f59e0b/ffffff?text=Angew");
        journal10.setDescription("Angewandte Chemie是化学领域的权威期刊，发表化学研究的重要成果。");
        journal10.setPublisher("Wiley-VCH");
        journal10.setIssn("1433-7851");

        // 经济
        Journal journal11 = new Journal();
        journal11.setId(11L);
        journal11.setTitle("The Quarterly Journal of Economics");
        journal11.setAuthor("Oxford University Press");
        journal11.setCategory("经济");
        journal11.setCover("https://via.placeholder.com/200x280/10b981/ffffff?text=QJE");
        journal11.setDescription("QJE是经济学领域的顶级期刊，发表经济学理论和实证研究。");
        journal11.setPublisher("Oxford University Press");
        journal11.setIssn("0033-5533");

        Journal journal12 = new Journal();
        journal12.setId(12L);
        journal12.setTitle("Journal of Political Economy");
        journal12.setAuthor("University of Chicago Press");
        journal12.setCategory("经济");
        journal12.setCover("https://via.placeholder.com/200x280/f59e0b/ffffff?text=JPE");
        journal12.setDescription("JPE是经济学领域的权威期刊，发表经济学理论和政策研究。");
        journal12.setPublisher("University of Chicago Press");
        journal12.setIssn("0022-3808");

        // 环境
        Journal journal13 = new Journal();
        journal13.setId(13L);
        journal13.setTitle("Environmental Science & Technology");
        journal13.setAuthor("American Chemical Society");
        journal13.setCategory("环境");
        journal13.setCover("https://via.placeholder.com/200x280/10b981/ffffff?text=EST");
        journal13.setDescription("EST是环境科学领域的顶级期刊，发表环境科学和技术方面的研究。");
        journal13.setPublisher("American Chemical Society");
        journal13.setIssn("0013-936X");

        Journal journal14 = new Journal();
        journal14.setId(14L);
        journal14.setTitle("Nature Sustainability");
        journal14.setAuthor("Nature Publishing Group");
        journal14.setCategory("环境");
        journal14.setCover("https://via.placeholder.com/200x280/22c55e/ffffff?text=Nat+Sust");
        journal14.setDescription("Nature Sustainability是环境可持续性领域的权威期刊，发表可持续发展相关研究。");
        journal14.setPublisher("Nature Publishing Group");
        journal14.setIssn("2398-9629");

        journals.add(journal1);
        journals.add(journal2);
        journals.add(journal3);
        journals.add(journal4);
        journals.add(journal5);
        journals.add(journal6);
        journals.add(journal7);
        journals.add(journal8);
        journals.add(journal9);
        journals.add(journal10);
        journals.add(journal11);
        journals.add(journal12);
        journals.add(journal13);
        journals.add(journal14);
    }

    // 获取所有期刊
    public List<Journal> getAllJournals() {
        return journals;
    }

    // 根据分类获取期刊
    public List<Journal> getJournalsByCategory(String category) {
        if ("全部".equals(category)) {
            return journals;
        }
        return journals.stream()
                .filter(journal -> journal.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    // 搜索期刊
    public List<Journal> searchJournals(String keyword) {
        return journals.stream()
                .filter(journal -> journal.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // 搜索并按分类筛选期刊
    public List<Journal> searchAndFilterJournals(String keyword, String category) {
        return journals.stream()
                .filter(journal -> {
                    boolean matchesKeyword = journal.getTitle().toLowerCase().contains(keyword.toLowerCase());
                    boolean matchesCategory = "全部".equals(category) || journal.getCategory().equals(category);
                    return matchesKeyword && matchesCategory;
                })
                .collect(Collectors.toList());
    }

    // 根据ID获取期刊
    public Optional<Journal> getJournalById(Long id) {
        return journals.stream()
                .filter(journal -> journal.getId().equals(id))
                .findFirst();
    }

    // 获取所有分类
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("全部");
        journals.stream()
                .map(Journal::getCategory)
                .distinct()
                .forEach(categories::add);
        return categories;
    }
}
