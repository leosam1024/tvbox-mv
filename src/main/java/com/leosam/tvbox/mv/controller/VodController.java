package com.leosam.tvbox.mv.controller;

import com.leosam.tvbox.mv.data.VodResult;
import com.leosam.tvbox.mv.service.MvService;
import com.leosam.tvbox.mv.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author admin
 * @since 2023/6/11 18:54
 */
@RestController
public class VodController {

    @Autowired
    private MvService mvService;

    @RequestMapping(value = {"/mv/vod"})
    public VodResult searchVod(String wd, String ac, String ids, String maxCount) throws Exception {
        int max = Math.min(Math.max(NumberUtils.toInt(maxCount, 100), 10), 1000);
        String query = StringUtils.hasText(wd) ? wd : ids;
        return mvService.searchVod(query, max);
    }

}
