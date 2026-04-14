package com.soulcurator.backend.controller;

import com.soulcurator.backend.model.SimpleWork;
import com.soulcurator.backend.repository.SimpleWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SimpleController {
    @Autowired 
    private SimpleWorkRepository repo;
    
    @GetMapping("/works")
    public List<SimpleWork> getAll() { 
        return repo.findAll(); 
    }
    
    @PostMapping("/works")
    public SimpleWork create(@RequestBody SimpleWork work) { 
        return repo.save(work); 
    }
    
    @GetMapping("/health")
    public String health() { 
        return "SoulCurator Backend is running!"; 
    }
    
    @GetMapping("/test-data")
    public String addTestData() {
        SimpleWork work1 = new SimpleWork();
        work1.setTitle("最后的生还者");
        work1.setType("GAME");
        work1.setDescription("末日生存游戏，讲述乔尔和艾莉的故事");
        repo.save(work1);
        
        SimpleWork work2 = new SimpleWork();
        work2.setTitle("星际穿越");
        work2.setType("MOVIE");
        work2.setDescription("关于时间、爱和星际旅行的科幻电影");
        repo.save(work2);
        
        SimpleWork work3 = new SimpleWork();
        work3.setTitle("三体");
        work3.setType("BOOK");
        work3.setDescription("刘慈欣的科幻小说，讲述地球与三体文明的接触");
        repo.save(work3);
        
        return "添加了3个测试作品！";
    }
}
