package com.ruoyi.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.Invoice;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISupplierService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SellDetail;
import com.ruoyi.system.service.ISellDetailService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * 销售订单列表Controller
 * 
 * @author ruoyi
 * @date 2020-05-20
 */
@Controller
@RequestMapping("/system/detail")
public class SellDetailController extends BaseController
{
    private String prefix = "system/detail";

    @Autowired
    private ISellDetailService sellDetailService;

    @Autowired
    private ISupplierService supplierService;

    List<SellDetail> sellDetailList=new ArrayList<>();

    @PostMapping("/importData")
    @Log(title = "销售订单列表", businessType = BusinessType.IMPORT)
    @RequiresPermissions("system:detail:import")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SellDetail> util = new ExcelUtil<SellDetail>(SellDetail.class);
        sellDetailList = util.importExcel(file.getInputStream());
        String message = sellDetailService.importUser(sellDetailList);
        return AjaxResult.success(message);
    }

    @RequiresPermissions("system:detail:view")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate()
    {
        ExcelUtil<SellDetail> util = new ExcelUtil<SellDetail>(SellDetail.class);
        return util.importTemplateExcel("销售商品数据");
    }


    @RequiresPermissions("system:detail:view")
    @GetMapping()
    public String detail()
    {
        return prefix + "/detail";
    }

    /**
     * 查询销售订单列表列表
     */
    @RequiresPermissions("system:detail:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SellDetail sellDetail)
    {
        startPage();
        List<SellDetail> list = sellDetailService.selectSellDetailList(sellDetail);
        return getDataTable(list);
    }

    /**
     * 导出销售订单列表列表
     */
    @RequiresPermissions("system:detail:export")
    @Log(title = "销售订单列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SellDetail sellDetail)
    {
        List<SellDetail> list = sellDetailService.selectSellDetailList(sellDetail);
        ExcelUtil<SellDetail> util = new ExcelUtil<SellDetail>(SellDetail.class);
        return util.exportExcel(list, "detail");
    }

    /**
     * 新增销售订单列表
     */
    @GetMapping("/add")
    public ModelAndView add(ModelAndView model)
    {
        model.addObject("supplierList",supplierService.findList());
        model.setViewName(prefix + "/add");
         return model;
    }

    /**
     * 新增保存销售订单列表
     */
    @RequiresPermissions("system:detail:add")
    @Log(title = "销售订单列表", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SellDetail sellDetail)
    {
        return toAjax(sellDetailService.insertSellDetail(sellDetail));
    }

    /**
     * 修改销售订单列表
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        SellDetail sellDetail = sellDetailService.selectSellDetailById(id);
        mmap.put("sellDetail", sellDetail);
        mmap.put( "supplierList",supplierService.findList());
        return prefix + "/edit";
    }

    /**
     * 修改保存销售订单列表
     */
    @RequiresPermissions("system:detail:edit")
    @Log(title = "销售订单列表", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SellDetail sellDetail)
    {
        return toAjax(sellDetailService.updateSellDetail(sellDetail));
    }




    @Log(title = "销售开票", businessType = BusinessType.UPDATE)
    @PostMapping("/makeinvoice/{id}")
    @ResponseBody
    public AjaxResult makeInvoice(@PathVariable("id") Long id)
    {

        return toAjax(sellDetailService.makeinvoice(id));
    }

    /**
     * 删除销售订单列表
     */
    @RequiresPermissions("system:detail:remove")
    @Log(title = "销售订单列表", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(sellDetailService.deleteSellDetailByIds(ids));
    }
}