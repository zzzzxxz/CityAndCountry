package com.example.demo.area;

import com.example.demo.area.entity.TChina;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zx
 */
@Data
public class ChinaTree implements Serializable {

    private static final long serialVersionUID = 1460122889486691189L;

    private List<TChina> TChinaList;

    public ChinaTree(List<TChina> TChinaList) {
        this.TChinaList = TChinaList;
    }

    //建立树形结构
    public List<TChina> buildTree() {
        List<TChina> children = new ArrayList<>();
        for (TChina chinaNode : getRootNode()) {
            buildChildTree(chinaNode);
            children.add(chinaNode);
        }
        return children;
    }

    //递归，建立子树形结构
    private TChina buildChildTree(TChina pNode) {
        List<TChina> children = new ArrayList<>();
        for (TChina china : TChinaList) {
            if (pNode.getId().equals(china.getParent())) {
                children.add(buildChildTree(china));
            }
        }
        pNode.setChildren(children);
        return pNode;
    }

    //获取根节点
    private List<TChina> getRootNode() {
        List<TChina> rootTChinaLists = new ArrayList<>();
        for (TChina chinaNode : TChinaList) {
            if (chinaNode.getParent() == null) {
                rootTChinaLists.add(chinaNode);
            }
        }
        return rootTChinaLists;
    }
}
