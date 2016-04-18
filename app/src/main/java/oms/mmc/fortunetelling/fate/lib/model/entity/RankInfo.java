package oms.mmc.fortunetelling.fate.lib.model.entity;

import java.util.List;

/**
 * Author: meyu
 * Date:   16/4/1
 * Email:  627655140@qq.com
 */
public class RankInfo {

    public String status;
    public List<Rank> content;

    @Override
    public String toString() {
        return "RankInfo{" +
                "content=" + content +
                ", status='" + status + '\'' +
                '}';
    }
}
