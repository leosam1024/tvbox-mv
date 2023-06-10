package com.leosam.tvbox.mv.controller;

import com.leosam.tvbox.mv.data.MvResult;
import com.leosam.tvbox.mv.service.MvService;
import com.leosam.tvbox.mv.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author admin
 * @since 2023/6/10 17:34
 */
@RestController
public class IndexController {

    @Autowired
    private MvService mvService;

    @RequestMapping(value = {"/mv/search"})
    public MvResult searchMv(String query, String maxCount) throws Exception {
        int max = Math.min(Math.max(NumberUtils.toInt(maxCount, 100), 10), 1000);
        return mvService.search(query, max);
    }

}
