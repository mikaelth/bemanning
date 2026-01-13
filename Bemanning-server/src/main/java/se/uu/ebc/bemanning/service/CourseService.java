package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import se.uu.ebc.luntan.vo.CourseInstanceVO;

@Service
public class CourseService {

    @Autowired
    RestClient luntanCIRestClient;


    public List<CourseInstanceVO> getCourseInstances() {
        Map<String,List<CourseInstanceVO>> courseInstances = luntanCIRestClient.get()
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});

       return courseInstances.get("cis");

    }

}
