package model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class MontrealJUG_A {

    public static void main(String[] args) {
        Set<Article> articles = Article.readAll();

        int size = articles.size();
        System.out.println("size = " + size);
        long count = articles.stream().count();
        System.out.println("count = " + count);

        articles.stream().collect(Collectors.counting());

        Integer minYear = articles.stream()
                .map(Article::getInceptionYear)
                .min(Comparator.naturalOrder())
                .orElseThrow();
        System.out.println("minYear = " + minYear);

        Integer minYear2 = articles.stream()
                .mapToInt(Article::getInceptionYear)
                .min()
                .orElseThrow();
        System.out.println("minYear2 = " + minYear2);


        articles.stream().filter(article -> article.getInceptionYear() == 1960)
                .map(Article::getTitle)
                .collect(Collectors.joining(" | "));

        System.out.println("------------------------------------------------------------------");
        Map<Integer, List<Article>> articlesPerYear = articles.stream()
                .collect(Collectors.groupingBy(Article::getInceptionYear));

        //  System.out.println("articlesPerYear.size() = " + articlesPerYear.size());
        // System.out.println("articlesPerYear = " + articlesPerYear);


        Map<Integer, Long> numberOfArticlesPerYear = articles.stream()
                .collect(Collectors.groupingBy(Article::getInceptionYear, Collectors.counting()));
        System.out.println("numberOfArticlesPerYear.size() = " + numberOfArticlesPerYear.size());
        System.out.println("numberOfArticlesPerYear = " + numberOfArticlesPerYear);


        Map.Entry<Integer, Long> yearWithTheMostArticle = numberOfArticlesPerYear
                .entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElseThrow();
        System.out.println("yearWithTheMostArticle = " + yearWithTheMostArticle);

        Map<Long, List<Integer>> mapp;

        Map<Long, List<Map.Entry<Integer, Long>>> yearPerArticles =
                numberOfArticlesPerYear
                        .entrySet().stream()
                        .collect(Collectors.groupingBy(
                                Map.Entry::getValue
                        ));
        System.out.println("yearPerArticles = " + yearPerArticles);

        Map<Long, List<Integer>> yearPerArticles2 =
                numberOfArticlesPerYear
                        .entrySet().stream()
                        .collect(Collectors.groupingBy(
                                Map.Entry::getValue,
                                Collectors.mapping(entry -> entry.getKey(),
                                        Collectors.toList())
                        ));
        System.out.println("yearPerArticles2 = " + yearPerArticles2);

        Map.Entry<Long, List<Integer>> bestYearPerArticles =
                numberOfArticlesPerYear
                        .entrySet().stream()
                        .collect(Collectors.groupingBy(
                                Map.Entry::getValue,
                                Collectors.mapping(entry -> entry.getKey(),
                                        Collectors.toList())
                        )).entrySet().stream().max(Map.Entry.comparingByKey()).orElseThrow();


        System.out.println("--------------------------groupingBy----------------------------");


        Collector<Author, ?, Map<Author, Long>> groupingBy =
                Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting());

        Map<Author, Long> map =
                articles.stream()
                        .flatMap(article -> article.getAuthors().stream())
                        .collect(
                                groupingBy);

        System.out.println("map.size() = " + map.size());

        Map.Entry<Author, Long> authorWithMostArticles = map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();
        System.out.println("authorWithMostArticles = " + authorWithMostArticles);

        Function<Map<Author, Long>, Map.Entry<Author, Long>> finisher =
                m -> m.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .orElseThrow();

        Collector<Author, ?, Map.Entry<Author, Long>> authorEntryCollector = Collectors.collectingAndThen(
                groupingBy, finisher
        );

        Map.Entry<Author, Long> authorWithMostArticle2 = articles.stream()
                .flatMap(article -> article.getAuthors().stream())
                .collect(authorEntryCollector);

        System.out.println("authorWithMostArticle2 = " + authorWithMostArticle2);

        System.out.println("-------------------------FLAPMAP-----------------------------");

        Collector<Article, ?, Map.Entry<Author, Long>> collector =
                Collectors.flatMapping(article -> article.getAuthors().stream(),
                        authorEntryCollector);

        Map.Entry<Author, Long> authorWithMostArticle3 = articles.stream()
//                .flatMap(article -> article.getAuthors().stream())
                .collect(collector);

        System.out.println("authorWithMostArticle3 = " + authorWithMostArticle3);


        System.out.println("------------------------------------------------------");

        //quelle est author qui ecrit plus d'article en une année
        //pour chaque année l'author qui a publie le plus
        Map<Integer, Map.Entry<Author, Long>> authorWithMostArticlesInYear = articles.stream()
                .collect(
                        Collectors.groupingBy(
                                Article::getInceptionYear,
                                collector
                        )
                );
        System.out.println("authorWithMostArticlesInYear = " + authorWithMostArticlesInYear);
//l'author qui a publie le plus
        Map.Entry<Integer, Map.Entry<Author, Long>> authorWithMostArticlesInYear2 = articles.stream()
                .collect(
                        Collectors.groupingBy(
                                Article::getInceptionYear,
                                collector
                        )
                ).entrySet().stream()
                .max(Comparator.comparing(e -> e.getValue().getValue())).orElseThrow();
        System.out.println("authorWithMostArticlesInYear2 = " + authorWithMostArticlesInYear2);


        Collector<Article, ?, Map<Integer, Map.Entry<Author, Long>>> c2 = Collectors.groupingBy(
                Article::getInceptionYear,
                collector
        );
        Function<Map<Integer, Map.Entry<Author, Long>>, Map.Entry<Integer, Map.Entry<Author, Long>>> finisher2 =
                f -> f.entrySet().stream()
                        .max(Comparator.comparing(e -> e.getValue().getValue())).orElseThrow();

        Collector<Article, ?, Map.Entry<Integer, Map.Entry<Author, Long>>> articleEntryCollector =
                Collectors.collectingAndThen(c2, finisher2);


        Map.Entry<Integer, Map.Entry<Author, Long>> authorWithMostArticlesInYear4 =
                articles.stream()
                        .collect(articleEntryCollector);
        System.out.println("authorWithMostArticlesInYear4= " + authorWithMostArticlesInYear4);

        Map.Entry<Integer, Map.Entry<Author, Long>> authorWithMostArticlesInYear3 = articles.stream()
                .collect(
                        c2
                ).entrySet().stream()
                .max(Comparator.comparing(e -> e.getValue().getValue())).orElseThrow();
        System.out.println("authorWithMostArticlesInYear3 = " + authorWithMostArticlesInYear3);
    }
}
