import React from "react";
import { Card } from "components";
import { Grid } from "@material-ui/core";
import { connect } from "react-redux";
import { fetchGeneralMDMSData } from "egov-ui-kit/redux/common/actions";
import { prepareFormData, loadMDMSData } from "egov-ui-kit/redux/common/actions";
import { toggleSpinner } from "egov-ui-kit/redux/common/actions";
import commonConfig from "config/common.js";
import { TextField } from "components";
import Field from "egov-ui-kit/utils/field";
import { getTenantId ,getFinalData} from "egov-ui-kit/utils/localStorageUtils";
import "./index.css";
import { prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import get from "lodash/get";
import set from "lodash/set";
import Label from "egov-ui-kit/utils/translationNode";
class DemandCollection extends React.Component {
  render() {
    const { prepareFinalObject, preparedFinalObject,Properties = [] } = this.props;
    const finalData=getFinalData();

   let firstdisplay_year = [];
   firstdisplay_year = get(preparedFinalObject, `DemandProperties[0].propertyDetails[0].demand[0].demand[${finalData[0].financialYear}]`, '');
   let demands_data = get(preparedFinalObject, `DemandProperties[0].propertyDetails[0].demand`, '');
   let dummyarray = [];

   if (firstdisplay_year.length === 0) {
    dummyarray[0] = null;

    if (demands_data[0] != null) {
     for (let i = 0; i < demands_data.length; i++) {
      if (demands_data[i] != null) {
       dummyarray[i + 1] = demands_data[i];
      }
     }
    } else {
     dummyarray = demands_data;

    }
   } else {
    dummyarray = demands_data;
   }


   //set(preparedFinalObject,`DemandProperties[0].propertyDetails[0].demand`,'');

   set(preparedFinalObject, `DemandProperties[0].propertyDetails[0].demand`, dummyarray);

   //console.log("prasad after preparedFinalObjec ", preparedFinalObject); 

    const getYear =
      finalData && finalData.length ? (
        finalData.map((data, index) => {
          return (
            <div>
              <div key={index}>{data.financialYear}</div>
              <Card
                key={index}
                style={{ backgroundColor: "white" }}
                textChildren={
                  <div className="pt-owner-info">
                    <div className={` col-sm-12`} key={index}>
                      <div className={`col-sm-6`}  style={{ zIndex:1000  }} >
                        <div className={`col-sm-12`} style={{ textAlign: "center" }}>
                        <Label
                          labelStyle={{ letterSpacing: "0.67px", color: "rgba(0, 0, 0, 0.87)", fontWeight: "400", lineHeight: "19px" }}
                          label={"PT_DEMAND" ? "PT_DEMAND" : "NA"}
                          fontSize="16px"
                        />
                        </div>
                        {data.taxHead.map((taxData, index1) => {                        
                           return (
                            <div className={`col-xs-12`}>
                              <TextField
                                floatingLabelText={<Label label={taxData.code}/>}
                                hintText={<Label label="PT_ENTER_AN_AMOUNT" />}
                                min={get(preparedFinalObject,`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_DEMAND`)}
                                max={get(preparedFinalObject,`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_DEMAND`)}
                                // min={taxData.isDebit?-99999:0}
                                // max={taxData.isDebit?-1:0}
                                type="number"
                                value={get(preparedFinalObject,`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_DEMAND`)}
                                onChange={(e) => {
                                  if (e.target.value.includes(".")) return
                                  let value = "";
                                  value = e.target.value;
                                  prepareFinalObject(`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_TAXHEAD`,taxData.code)
                                  prepareFinalObject(`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_DEMAND`, taxData.isDebit?(Math.sign(value)===-1?value:-value):value)
                                }}
                                onWheel={event => { event.preventDefault(); }}
                              />
                            </div>
                          );
                        })}
                      </div>
                      <div className={`col-sm-6`}>
                        <div className={`col-sm-12`} style={{ textAlign: "center" }}>
                        <Label
                          labelStyle={{ letterSpacing: "0.67px", color: "rgba(0, 0, 0, 0.87)", fontWeight: "400", lineHeight: "19px" }}
                          label={"PT_COLLECTED" ? "PT_COLLECTED" : "NA"}
                          fontSize="16px"
                        />
                        </div>
                        {data.taxHead.map((taxData, index1) => {
                          return (
                            <div className={`col-xs-12`} key={index1}>
                              <TextField
                                key={index1}
                                type="number"
                                min={get(preparedFinalObject,`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_COLLECTED`)}
                                max={get(preparedFinalObject,`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_COLLECTED`)}
                                value={get(preparedFinalObject,`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_COLLECTED`)}
                                floatingLabelText={<Label label={taxData.code}/>}
                                hintText={<Label label="PT_ENTER_AN_AMOUNT"/>}

                                onChange={(e) => {
                                  if (e.target.value.includes(".")) return
                                  prepareFinalObject(`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_TAXHEAD`,taxData.code);
                                  prepareFinalObject(`DemandProperties[0].propertyDetails[0].demand[${index}].demand[${data.financialYear}][${index1}].PT_COLLECTED`, e.target.value);
                                }}
                                onWheel={event => { event.preventDefault(); }}
                                disabled={taxData.isDebit}
                              />
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  </div>
                }
              />
              <br />
            </div>
          );
        })
      ) : (
        <div>error </div>
      );
    const textdata = () => {};

    return <div>{getYear}</div>;
  }
}
const mapStateToProps = (state) => {
  const { common, form, screenConfiguration } = state;
  const { generalMDMSDataById, loadMdmsData } = common;
  let { preparedFinalObject = {} } = screenConfiguration;
  preparedFinalObject={...preparedFinalObject};
  const { Properties } = preparedFinalObject || {};
  const FinancialYear = generalMDMSDataById && generalMDMSDataById.FinancialYear;
  const getYearList = FinancialYear && Object.keys(FinancialYear);

  return { getYearList, form, Properties,preparedFinalObject };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchGeneralMDMSData: (requestBody, moduleName, masterName) => dispatch(fetchGeneralMDMSData(requestBody, moduleName, masterName)),
    removeForm: (formkey) => dispatch(removeForm(formkey)),
    toggleSpinner: () => dispatch(toggleSpinner()),
    prepareFormData: (path, value) => dispatch(prepareFormData(path, value)),
    loadMDMSData: (requestBody, moduleName, masterName) => dispatch(loadMDMSData(requestBody, moduleName, masterName)),
    reset_property_reset: () => dispatch(reset_property_reset()),
    prepareFinalObject: (jsonPath, value) => dispatch(prepareFinalObject(jsonPath, value))
  };
};
export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DemandCollection);
